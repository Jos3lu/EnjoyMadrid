import { Point } from "./point.model";
import { User } from "./user.model";

export interface Route {
    id?: number;
    name: string;
    date: Date;
    points?: Point[];
    user?: User;
}