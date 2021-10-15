package com.thebois.models.world.resources;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

public class RockTests {

    @Test
    public void getTypeFromRockResource() {
        // Arrange
        final Rock rock = new Rock(1, 1);

        // Act
        final ResourceType resourceType = rock.getType();

        // Assert
        assertThat(resourceType).isEqualTo(ResourceType.ROCK);
    }

    @Test
    public void getDeepCloneShouldBeEqualToOriginal() {
        // Arrange
        final Rock rock = new Rock(1, 1);

        // Act
        final IResource deepClone = rock.deepClone();

        // Assert
        assertThat(deepClone).isEqualTo(rock);
    }

    @Test
    public void getCostIsFloatMax() {
        // Arrange
        final Rock rock = new Rock(1, 1);
        final float expectedValue = Float.MAX_VALUE;

        // Act
        final float actualValue = rock.getCost();

        // Assert
        assertThat(actualValue).isEqualTo(expectedValue);
    }

}