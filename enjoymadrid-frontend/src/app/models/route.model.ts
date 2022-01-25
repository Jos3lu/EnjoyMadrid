import { PointModel } from "./point-model";

export interface RouteModel {
    id?: number;
    name: string;
    preferences: {[key: string]: number};
    maxDistance: number;
    origin: PointModel;
    destination: PointModel;
    totalDist?: number;
    totalTime?: number;
    date?: Date; 
    transports: string[];
}