export interface SegmentModel {
    id?: number;
    source: number;
    target: number;
    distance: number;
    duration: number;
    transportMode: string;
    steps: {[key: string]: string};
    polyline: number[][];
}