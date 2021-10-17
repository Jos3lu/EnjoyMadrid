import { Route } from "./route.model";

export interface User {
    id?: number;
    name?: string;
    username: string;
    password?: string;
    routes?: Route[];
}