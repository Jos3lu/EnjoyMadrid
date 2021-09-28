import { Route } from "./route.model";


export interface User {
    id?: number;
    name: string;
    email: string;
    password: string;
    routes?: Route[];
}