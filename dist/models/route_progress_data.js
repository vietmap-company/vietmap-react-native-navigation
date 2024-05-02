function fromJson(json) {
    const parsedJson = JSON.parse(json);
    const dataString = parsedJson.data;
    const data = JSON.parse(dataString);
    return data;
}
// fromJson function to parse JSON object and return NavigationData object
export function fromNavigationProgressDataJson(json) {
    return JSON.parse(json);
}
