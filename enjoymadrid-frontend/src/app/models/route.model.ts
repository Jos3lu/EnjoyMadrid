export interface RouteModel {
    id?: number;
    name: string;
    preferences?: Map<string, number>;
    maxDist?: number;
    origin?: any;
    destination?: any;
    totalDist?: number;
    totalTime?: number;
    date?: Date;
}