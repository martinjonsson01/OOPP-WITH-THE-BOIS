package com.thebois.models.beings.roles;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import com.thebois.models.beings.actions.IAction;
import com.thebois.models.beings.actions.IActionGenerator;
import com.thebois.models.world.IWorld;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

public class RoleTests {

    public static Stream<Arguments> getEqualRoles() {
        final LumberjackRole sameLumberjack = new LumberjackRole();
        return Stream.of(
            Arguments.of(sameLumberjack, sameLumberjack),
            Arguments.of(new LumberjackRole(), new LumberjackRole()),
            Arguments.of(new FarmerRole(), new FarmerRole()),
            Arguments.of(new GuardRole(), new GuardRole()),
            Arguments.of(new FisherRole(), new FisherRole()),
            Arguments.of(new BuilderRole(), new BuilderRole()),
            Arguments.of(new IdleRole(mock(IWorld.class)), new IdleRole(mock(IWorld.class))));
    }

    public static Stream<Arguments> getUnequalRoles() {
        return Stream.of(
            Arguments.of(new LumberjackRole(), new FarmerRole()),
            Arguments.of(new FarmerRole(), new LumberjackRole()),
            Arguments.of(new FisherRole(), new BuilderRole()),
            Arguments.of(new BuilderRole(), new FisherRole()),
            Arguments.of(new BuilderRole(), null));
    }

    public static Stream<Arguments> getRoleAndNames() {
        return Stream.of(
            Arguments.of(new LumberjackRole(), "Lumberjack"),
            Arguments.of(new FarmerRole(), "Farmer"),
            Arguments.of(new GuardRole(), "Guard"));
    }

    @BeforeEach
    public void setup() {
        RoleFactory.setWorld(mock(IWorld.class));
    }

    @AfterEach
    public void teardown() {
        RoleFactory.setWorld(null);
    }

    @ParameterizedTest
    @MethodSource("getRoleAndNames")
    public void getNameIsExpectedName(final AbstractRole role, final String expectedName) {
        // Act
        final String actualName = role.getName();

        // Assert
        assertThat(actualName).isEqualTo(expectedName);
    }

    @ParameterizedTest
    @MethodSource("getEqualRoles")
    public void equalsReturnsTrueWhenEqual(final AbstractRole first, final AbstractRole second) {
        // Assert
        assertThat(first).isEqualTo(second);
    }

    @ParameterizedTest
    @MethodSource("getUnequalRoles")
    public void equalsReturnsFalseWhenUnequal(final AbstractRole first, final AbstractRole second) {
        // Assert
        assertThat(first).isNotEqualTo(second);
    }

    @Test
    public void deepCloneCreatesCloneThatIsEqual() {
        // Arrange
        final AbstractRole original = RoleFactory.lumberjack();

        // Act
        final AbstractRole deepClone = original.deepClone();

        // Assert
        assertThat(deepClone).isEqualTo(original);
    }

    @Test
    public void obtainNextTaskReturnsTasksInOrderWhenTasksAreCompletedInSequence() {
        // Arrange
        final IAction first = mockTask(false);
        final IAction second = mockTask(false);
        final IAction third = mockTask(false);
        final AbstractRole role = mockTestRole(first, second, third);

        // Act
        final IAction actualFirst = role.obtainNextTask();
        // Simulate first task completing between this call and the next.
        when(first.isCompleted()).thenReturn(true);
        final IAction actualSecond = role.obtainNextTask();
        // Simulate second task completing between this call and the next.
        when(actualSecond.isCompleted()).thenReturn(true);
        final IAction actualThird = role.obtainNextTask();

        // Assert
        final List<IAction> actualTasks = List.of(actualFirst, actualSecond, actualThird);
        assertThat(actualTasks).containsExactly(first, second, third);
    }

    private IAction mockTask(final boolean completed) {
        final IAction task = mock(IAction.class);
        when(task.isCompleted()).thenReturn(completed);
        return task;
    }

    private AbstractRole mockTestRole(final IAction... params) {
        final List<IAction> tasks = List.of(params);
        final List<IActionGenerator> taskGenerators =
            tasks.stream().map(task -> (IActionGenerator) () -> task).collect(Collectors.toList());
        return new TestRole(taskGenerators);
    }

    @Test
    public void obtainNextTaskSkipsOverAlreadyCompletedTasks() {
        // Arrange
        final IAction completedTask1 = mockTask(true);
        final IAction completedTask2 = mockTask(true);
        final IAction uncompletedTask = mockTask(false);
        final AbstractRole role = mockTestRole(completedTask1, completedTask2, uncompletedTask);

        // Act
        final IAction actualTask = role.obtainNextTask();

        // Assert
        assertThat(actualTask).isEqualTo(uncompletedTask);
    }

    @Test
    public void obtainNextTaskReturnsSameTaskWhenItIsNotYetCompleted() {
        // Arrange
        final IAction uncompletedTask = mockTask(false);
        final IAction nextTask = mockTask(false);
        final AbstractRole role = mockTestRole(uncompletedTask, nextTask);

        // Act
        final IAction actualTaskFirstTime = role.obtainNextTask();
        final IAction actualTaskSecondTime = role.obtainNextTask();

        // Assert
        assertThat(actualTaskFirstTime).isEqualTo(actualTaskSecondTime).isEqualTo(uncompletedTask);
    }

    /**
     * Fake role that returns a custom set of task-generating actionables.
     */
    private static class TestRole extends AbstractRole {

        private final Collection<IActionGenerator> taskGenerators;

        TestRole(final Collection<IActionGenerator> taskGenerators) {
            this.taskGenerators = taskGenerators;
        }

        @Override
        public RoleType getType() {
            return null;
        }

        @Override
        protected Collection<IActionGenerator> getTaskGenerators() {
            return taskGenerators;
        }

    }

}
