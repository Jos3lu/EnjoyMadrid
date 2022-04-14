import { PointModel } from "./point.model";
import { SegmentModel } from "./segment.model";

export interface RouteResultModel {
    id?: number;
    name: string;
    points: PointModel[];
    duration: number;
    segments: SegmentModel[]; 
}