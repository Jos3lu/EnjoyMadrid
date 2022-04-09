import { PointModel } from "./point.model";
import { SegmentModel } from "./segment.model";

export interface RouteResultModel {
    name: string;
    points: PointModel[];
    duration: number;
    segments: SegmentModel[]; 
}