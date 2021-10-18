package com.thebois.models;

import java.util.stream.Stream;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import com.thebois.models.world.structures.IStructure;
import com.thebois.models.world.structures.StructureFactory;
import com.thebois.models.world.structures.StructureType;

import static org.assertj.core.api.Assertions.*;

public class PositionTests {

    public static Stream<Arguments> getPositionsAndDistances() {
        return Stream.of(
            Arguments.of(new Position(0, 0), new Position(0, 0), 0f),
            Arguments.of(new Position(0, 0), new Position(1, 1), 1.41421356f),
            Arguments.of(new Position(-123, -456), new Position(123, 456), 944.5952f),
            Arguments.of(new Position(1, 0), new Position(2, 0), 1f),
            Arguments.of(new Position(0, 1), new Position(0, 2), 1f),
            Arguments.of(new Position(-20, -30), new Position(0, 0), 36.0555f));
    }

    public static Stream<Arguments> getPositionsAndManhattanDistances() {
        return Stream.of(
            Arguments.of(new Position(0, 0), new Position(0, 0), 0),
            Arguments.of(new Position(0, 0), new Position(1, 1), 2),
            Arguments.of(new Position(-123, -456), new Position(123, 456), 1158),
            Arguments.of(new Position(1, 0), new Position(2, 0), 1),
            Arguments.of(new Position(0, 1), new Position(0, 2), 1),
            Arguments.of(new Position(-20, -30), new Position(0, 0), 50));
    }

    @Test
    public void constructorSetsCoordinates() {
        // Arrange
        final float x = 1.5f;
        final float y = 2.8f;

        // Act
        final Position position = new Position(x, y);

        // Assert
        assertThat(position.getX()).isEqualTo(x);
        assertThat(position.getY()).isEqualTo(y);
    }

    @Test
    public void getSetPositionChangesCoordinates() {
        // Arrange
        final Position position = new Position(0, 0);
        final int x = 123;
        final int y = 456;

        // Act
        position.setX(x);
        position.setY(y);

        // Assert
        assertThat(position.getX()).isEqualTo(x);
        assertThat(position.getY()).isEqualTo(y);
    }

    @Test
    public void positionEqualsToItSelfIsTrue() {
        // Arrange
        final Position position = new Position(123, 123);

        // Act
        final boolean isEqual = position.equals(position);

        // Assert
        assertThat(isEqual).isTrue();
    }

    @Test
    public void positionEqualNullIsFalse() {
        // Arrange
        final Position position = new Position(123, 123);

        // Act
        final boolean isEqual = position.equals(null);

        // Assert
        assertThat(isEqual).isFalse();
    }

    @Test
    public void positionEqualOtherObjectIsFalse() {
        // Arrange
        final Position position = new Position(123, 123);
        final IStructure house = StructureFactory.createStructure(StructureType.HOUSE, 0, 0);

        // Act
        final boolean isEqual = position.equals(house);

        // Assert
        assertThat(isEqual).isFalse();
    }

    @Test
    public void positionEqualsToIsTrueIfCoordinatesAreSame() {
        // Arrange
        final Position position1 = new Position(123, 123);
        final Position position2 = new Position(123, 123);

        // Act
        final boolean isEqual = position1.equals(position2);

        // Assert
        assertThat(isEqual).isTrue();
    }

    @Test
    public void positionEqualsIsFalseIfDifferentCoordinateX() {
        // Arrange
        final Position position1 = new Position(1, 1);
        final Position position2 = new Position(2, 1);

        // Act
        final boolean isEqual = position1.equals(position2);

        // Assert
        assertThat(isEqual).isFalse();
    }

    @Test
    public void positionEqualsIsFalseIfDifferentCoordinateY() {
        // Arrange
        final Position position1 = new Position(1, 1);
        final Position position2 = new Position(1, 2);

        // Act
        final boolean isEqual = position1.equals(position2);

        // Assert
        assertThat(isEqual).isFalse();
    }

    @Test
    public void positionHashCodeIsSameIfIdenticalPositions() {
        // Arrange
        final Position position1 = new Position(1, 1);
        final Position position2 = new Position(1, 1);

        // Act
        final boolean isEqual = position1.hashCode() == position2.hashCode();

        // Assert
        assertThat(isEqual).isTrue();
    }

    @Test
    public void positionHashCodeIsDifferentIfDifferentPositions() {
        // Arrange
        final Position position1 = new Position(1, 1);
        final Position position2 = new Position(2, 2);

        // Act
        final boolean isEqual = position1.hashCode() == position2.hashCode();

        // Assert
        assertThat(isEqual).isFalse();
    }

    @ParameterizedTest
    @MethodSource("getPositionsAndDistances")
    public void distanceToReturnsCorrectDistance(
        final Position first, final Position second, final float expectedDistance) {
        // Act
        final float actualDistance = first.distanceTo(second);

        // Assert
        assertThat(actualDistance).isCloseTo(expectedDistance, Assertions.offset(0.001f));
    }

    @ParameterizedTest
    @MethodSource("getPositionsAndManhattanDistances")
    public void manhattanDistanceToReturnsCorrectDistance(
        final Position first, final Position second, final int expectedDistance) {
        // Act
        final int actualDistance = first.manhattanDistanceTo(second);

        // Assert
        assertThat(actualDistance).isEqualTo(expectedDistance);
    }

}
