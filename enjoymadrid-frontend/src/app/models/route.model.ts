export interface RouteModel {
    id?: number;
    name: string;
    preferences?: Map<string, number>;
    maxDist?: number;
    totalDist?: number;
    totalTime?: number;
    date?: Date;
}