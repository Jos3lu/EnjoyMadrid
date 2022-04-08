import { PointModel } from "./point.model";
import { SegmentModel } from "./segment.model";

export interface RouteResponseModel {
    name: string;
    points: PointModel[];
    duration: number;
    segments: SegmentModel[]; 
}