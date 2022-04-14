import { PointModel } from "./point.model";

export interface RouteModel {
    id?: number;
    name: string;
    preferences: {[key: string]: number};
    maxDistance: number;
    transports: string[];
    origin: PointModel;
    destination: PointModel;
    date?: string; 
}