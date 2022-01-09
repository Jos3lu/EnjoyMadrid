import { TransportPointModel } from "./transport-point.model";

export interface RouteModel {
    id?: number;
    name: string;
    preferences: {[key: string]: number};
    maxDistance: number;
    origin: TransportPointModel;
    destination: TransportPointModel;
    totalDist?: number;
    totalTime?: number;
    date?: Date; 
    transports: string[];
}