# Navigation Progress Data Model
Certainly! Here's a hierarchical representation of the provided navigation progress data model in the requested format:
## Model data structure
- NavigationProgressData
    - nativeEvent
        - data
            - distanceRemaining
            - durationRemaining
            - distanceTraveled
            - legIndex
            - currentLeg
                - distance
                - expectedTravelTime
                - steps
                    - CurrentStep
                        - instructions
                        - distance
                        - expectedTravelTime
            - currentLegDistanceRemaining
            - currentLegDistanceTraveled
            - currentStepInstruction
            - distanceToNextTurn
            - currentModifier
            - currentModifierType
            - location
                - latitude
                - longitude
                - provider
                - speed
                - bearing
                - altitude
                - accuracy
                - speedAccuracyMetersPerSecond
            - snappedLocation
                - (Same as location properties)
# Model data description:

## CurrentStep Interface
Represents current step in a leg with instructions, distance, and expected travel time.

- **instructions**: Detailed instructions for the current step, guiding the user on what action to take.
- **distance**: The distance to be covered in this step, measured in meters.
- **expectedTravelTime**: The expected time it will take to complete this step, measured in seconds.

## CurrentLeg Interface
Represents a leg of the journey with distance, expected travel time, and steps.

- **distance**: The total distance of the leg, measured in meters.
- **expectedTravelTime**: The expected total time to complete the leg, measured in seconds.
- **steps**: An array of steps composing the leg, each representing a segment of the journey.

## Location Interface
Represents a location with latitude, longitude, and other details.

- **latitude**: The latitude coordinate of the location.
- **longitude**: The longitude coordinate of the location.
- **provider**: The source or provider of the location data.
- **speed**: The speed of the entity at this location.
- **bearing**: The compass bearing or direction of movement at this location, measured in degrees.
- **altitude**: The altitude or elevation of the location above sea level.
- **accuracy**: The accuracy of the location data, indicating the radius within which the true location is likely to be found.
- **speedAccuracyMetersPerSecond**: The accuracy of the speed measurement.

## SnappedLocation Interface
Extends Location to represent a snapped location.

## NavigationProgressData Interface
Represents the main navigation data structure.

- **nativeEvent**:
    
    This default property of react native, which contains the data from the Android/iOS platform
  - **data**:
    - **distanceRemaining**: The remaining distance to the destination from the current location.
    - **durationRemaining**: The remaining time to reach the destination from the current location.
    - **distanceTraveled**: The total distance traveled from the starting point to the current location.
    - **legIndex**: The index of the current leg within the entire journey.
    - **currentLegDistanceRemaining**: The remaining distance to complete the current leg.
    - **currentLegDistanceTraveled**: The distance already covered in the current leg.
    - **currentStepInstruction**: The instruction for the current step, guiding the user on the next action.
    - **distanceToNextTurn**: The distance to the next change in direction or turn. Value response in meter.
    - **currentModifier**: The current modifier indicating any additional information about the current step, such as 'left', 'right', etc.
    - **currentModifierType**: The type of the current modifier, providing additional context about the modifier, such as 'rotary', 'roundabout', etc.
    - **currentLeg**: The details of the current leg of the journey.
    - **location**: The current location of the user.
    - **snappedLocation**: The snapped location of the user, if available.
