export interface SegmentModel {
    id?: number;
    source: number;
    target: number;
    distance?: number;
    duration?: number;
    transportMode: string;
    steps?: string[];
    polyline: number[][];
    line?: string;
}