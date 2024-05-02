export function fromProgressChangeDataJson(json) {
    try {
        const parsedJson = JSON.parse(json);
        return {
            routeData: Object.assign(Object.assign({}, parsedJson.data), { currentLeg: Object.assign(Object.assign({}, parsedJson.data.currentLeg), { steps: parsedJson.data.currentLeg.steps.map((step) => (Object.assign({}, step))) }), location: Object.assign({}, parsedJson.data.location), snappedLocation: Object.assign({}, parsedJson.data.snappedLocation) }),
            eventType: parsedJson.eventType,
        };
    }
    catch (error) {
        console.error("Error parsing JSON:", error);
        return null;
    }
}
