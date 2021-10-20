package com.thebois.models.beings.actions;

import java.util.List;
import java.util.stream.Stream;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.ArgumentCaptor;

import com.thebois.models.Position;
import com.thebois.models.beings.IActionPerformer;
import com.thebois.models.inventory.items.IItem;
import com.thebois.models.inventory.items.ItemType;
import com.thebois.models.world.structures.IStructure;
import com.thebois.testutils.MockFactory;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

public class BuildActionTests {

    private IActionPerformer performer;
    private IStructure structure;
    private IAction action;

    public static Stream<Arguments> getEqualBuilds() {
        final IStructure sameStructure = MockFactory.createStructure(new Position(), false);
        final IAction sameInstance = ActionFactory.createBuild(sameStructure);
        return Stream.of(Arguments.of(sameInstance, sameInstance),
                         Arguments.of(ActionFactory.createBuild(sameStructure),
                                      ActionFactory.createBuild(sameStructure)));
    }

    public static Stream<Arguments> getNotEqualBuilds() {
        final IStructure sameStructure = MockFactory.createStructure(new Position(), false);
        return Stream.of(Arguments.of(ActionFactory.createBuild(mock(IStructure.class)),
                                      ActionFactory.createBuild(sameStructure)),
                         Arguments.of(ActionFactory.createBuild(sameStructure), null),
                         Arguments.of(ActionFactory.createBuild(sameStructure),
                                      mock(IAction.class)));
    }

    @BeforeEach
    public void setup() {
        performer = mock(IActionPerformer.class);
        structure = MockFactory.createStructure(new Position(10, 10), false);
        action = ActionFactory.createBuild(structure);
    }

    @Test
    public void performDeliversAllNeededItemsToStructure() {
        // Arrange
        final Position besidesStructure = structure
            .getPosition()
            .subtract(1, 0);
        when(performer.getPosition()).thenReturn(besidesStructure);

        when(structure.tryDeliverItem(any())).thenReturn(true);
        final List<ItemType> neededItemTypes = List.of(ItemType.LOG, ItemType.LOG, ItemType.ROCK);
        when(structure.getNeededItems()).thenReturn(neededItemTypes);

        // Act
        action.perform(performer);

        // Assert
        final ArgumentCaptor<IItem> itemCaptor = ArgumentCaptor.forClass(IItem.class);
        verify(structure, atLeastOnce()).tryDeliverItem(itemCaptor.capture());

        final List<IItem> deliveredItems = itemCaptor.getAllValues();
        assertThat(deliveredItems)
            .map(IItem::getType)
            .containsExactlyElementsOf(neededItemTypes);
    }

    @Test
    public void isCompletedReturnsSameAsStructureIsCompleted() {
        // Arrange
        final boolean completedFirstTime = true;
        final boolean completedSecondTime = false;
        when(structure.isCompleted()).thenReturn(completedFirstTime, completedSecondTime);

        // Act
        final boolean isCompletedFirstTime = action.isCompleted(performer);
        final boolean isCompletedSecondTime = action.isCompleted(performer);

        // Assert
        assertThat(isCompletedFirstTime).isEqualTo(completedFirstTime);
        assertThat(isCompletedSecondTime).isEqualTo(completedSecondTime);
    }

    @Test
    public void canPerformReturnsTrueWhenNearby() {
        // Arrange
        final Position besideStructure = structure
            .getPosition()
            .subtract(1, 0);
        when(performer.getPosition()).thenReturn(besideStructure);

        // Act
        final boolean canPerform = action.canPerform(performer);

        // Assert
        assertThat(canPerform).isTrue();
    }

    @Test
    public void canPerformReturnsFalseWhenFarAway() {
        // Arrange
        final Position awayFromStructure = structure
            .getPosition()
            .add(10, 10);
        when(performer.getPosition()).thenReturn(awayFromStructure);

        // Act
        final boolean canPerform = action.canPerform(performer);

        // Assert
        assertThat(canPerform).isFalse();
    }

    @ParameterizedTest
    @MethodSource("getEqualBuilds")
    public void equalsIsTrueForEquals(final IAction first, final IAction second) {
        // Assert
        assertThat(first).isEqualTo(second);
    }

    @ParameterizedTest
    @MethodSource("getNotEqualBuilds")
    public void equalsIsFalseForNotEquals(final IAction first, final IAction second) {
        // Assert
        assertThat(first).isNotEqualTo(second);
    }

}