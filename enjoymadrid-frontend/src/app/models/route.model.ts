export interface RouteModel {
    id?: number;
    name: string;
    preferences?: Map<string, number>;
    maxDist?: number;
    origin?: string;
    destination?: string;
    totalDist?: number;
    totalTime?: number;
    date?: Date; 
    transports?: string[];
}