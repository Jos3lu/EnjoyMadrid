import { PointModel } from "./point-model";
import { SegmentModel } from "./segment.model";

export interface RouteModel {
    id?: number;
    name: string;
    preferences: {[key: string]: number};
    maxDistance: number;
    transports: string[];
    origin: PointModel;
    destination: PointModel;
    distance?: number;
    duration?: number;
    date?: Date; 
    points?: PointModel[];
    lines?: {[key: string]: string};
    segments?: SegmentModel[];
}