export interface RouteModel {
    id?: number;
    name: string;
    preferences?: {[key: string]: number};
    maxDistance?: number;
    origin?: string;
    destination?: string;
    totalDist?: number;
    totalTime?: number;
    date?: Date; 
    transports?: string[];
}