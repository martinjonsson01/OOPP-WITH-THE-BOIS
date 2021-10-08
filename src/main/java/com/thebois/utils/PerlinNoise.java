package com.thebois.utils;

import com.thebois.models.world.generation.patterns.IGenerationPattern;
import com.thebois.models.world.generation.patterns.LargeChunks;

/**
 * Generator used to generate Perlin Noise.
 */
public class PerlinNoise {

    private IGenerationPattern settings;
    private int currentOctave;
    private int seed;
    private final int[] primeNumberArray = {
        15731,
        789221,
        1376312589,
        547063007,
        522882781,
        286040101,
        628595861,
        426993577,
        360599501,
        795799423,
        185183639,
        719087363,
        249311663,
        };

    /**
     * Instantiate a Perlin Noise Generator with given arguments.
     *
     * @param settings The settings used to generate the perlin noise.
     * @param seed     A number used to generate the random table.
     */
    public PerlinNoise(
        final IGenerationPattern settings, final int seed) {
        setSettings(settings);
        setSeed(seed);
    }

    /**
     * Instantiate a Perlin Noise Generator.
     */
    public PerlinNoise() {
        this(new LargeChunks(), 0);
    }

    /**
     * Generates a float value with Perlin Noise algorithm given a position.
     *
     * @param coordinateX X coordinate for the position.
     * @param coordinateY Y coordinate for the position.
     *
     * @return The Perlin Noise value.
     */
    public float sample(final float coordinateX, final float coordinateY) {
        double total = 0;
        final int octaves = settings.getOctave();
        final double persistence = settings.getPersistence();
        final double frequency = settings.getFrequency();
        final double amplitude = settings.getAmplitude();
        for (int i = 0; i < octaves; i++) {
            currentOctave = i;
            final double octaveAmplification = Math.pow(persistence, i);
            final double frequencyOffSet = frequency * Math.pow(2, i);

            total = total + interpolateNoise(coordinateX * frequencyOffSet + seed,
                                             coordinateY * frequencyOffSet + seed)
                            * octaveAmplification;
        }
        return (float) (total * amplitude);
    }

    /**
     * Create a perlin noise by interpolating noise using Perlin noise algorithm.
     *
     * @param coordinateX X coordinate for the noise map, should not be integer as that will return
     *                    same perlin noise.
     * @param coordinateY Y coordinate for the noise map, should not be integer as that will return
     *                    same perlin noise.
     *
     * @return The Perlin noise
     */
    private double interpolateNoise(final double coordinateX, final double coordinateY) {
        // Convert to integers
        final int integerX = (int) coordinateX;
        final int integerY = (int) coordinateY;

        // Get fractals
        final double fractalX = coordinateX - integerX;
        final double fractalY = coordinateY - integerY;

        // Get gradients
        final double vector1 = smoothNoise(integerX, integerY);
        final double vector2 = smoothNoise(integerX + 1, integerY);
        final double vector3 = smoothNoise(integerX, integerY + 1);
        final double vector4 = smoothNoise(integerX + 1, integerY + 1);

        // Interpolate gradients
        final double interpolation1 = interpolate(vector1, vector2, fractalX);
        final double interpolation2 = interpolate(vector3, vector4, fractalX);

        return interpolate(interpolation1, interpolation2, fractalY);
    }

    /**
     * Linear Interpolation between two values.
     *
     * @param value1     The first value to interpolate.
     * @param value2     The second value to interpolate.
     * @param alphaValue A value that scale the interpolation.
     *
     * @return The interpolated value.
     */
    private double interpolate(final double value1, final double value2, final double alphaValue) {
        return value1 + alphaValue * (value2 - value1);
    }

    private double smoothNoise(final int coordinateX, final int coordinateY) {
        // Corners
        final float corner1 = noise(coordinateX - 1, coordinateY - 1);
        final float corner2 = noise(coordinateX - 1, coordinateY + 1);
        final float corner3 = noise(coordinateX + 1, coordinateY - 1);
        final float corner4 = noise(coordinateX + 1, coordinateY + 1);

        final double corners = (corner1 + corner2 + corner3 + corner4) / 16;

        // Sides
        final float side1 = noise(coordinateX - 1, coordinateY);
        final float side2 = noise(coordinateX + 1, coordinateY);
        final float side3 = noise(coordinateX, coordinateY - 1);
        final float side4 = noise(coordinateX, coordinateY + 1);

        final float sides = (side1 + side2 + side3 + side4) / 8;

        // Center
        final float center = noise(coordinateX, coordinateY) / 4;

        return center + sides + corners;
    }

    /**
     * Generates a pseudo random value with given position and current octave.
     *
     * @param coordinateX X coordinate for the position.
     * @param coordinateY Y coordinate for the position.
     *
     * @return The generated noise.
     */
    private float noise(final int coordinateX, final int coordinateY) {
        int temporaryValue1;
        int temporaryValue2;

        final int variable1 = 57;
        final int variable2 = 13;
        final int variable3 = 0x7fffffff;
        final int variable4 = 1073741824;
        final int resetValue = primeNumberArray.length / 3;

        final int primeNumber1 = primeNumberArray[currentOctave % resetValue];
        final int primeNumber2 = primeNumberArray[1 + currentOctave % resetValue];
        final int primeNumber3 = primeNumberArray[2 + currentOctave % resetValue];

        temporaryValue1 = coordinateX + coordinateY * variable1;
        temporaryValue1 = (temporaryValue1 << variable2) ^ temporaryValue1;
        temporaryValue2 = temporaryValue1 * temporaryValue1 * primeNumber1 + primeNumber2;
        temporaryValue2 = temporaryValue1 * temporaryValue2 + primeNumber3;

        temporaryValue1 = temporaryValue2 & variable3;

        return 1.0f - temporaryValue1 / (float) variable4;
    }

    public void setSeed(final int seed) {
        this.seed = seed;
    }

    public void setSettings(final IGenerationPattern settings) {
        this.settings = settings;
    }

}
