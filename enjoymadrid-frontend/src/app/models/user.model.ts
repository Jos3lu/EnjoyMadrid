import { Byte } from "@angular/compiler/src/util";
import { Route } from "./route.model";

export interface User {
    id?: number;
    name?: string;
    username?: string;
    password?: string;
    photo?: Byte[];
    routes?: Route[];
}