//import { Route } from "./route.model";

export interface Point {
    id?: number;
    longitude: number;
    latitude: number;
    name: string;
    address?: string;
    zipcode?: number;
    phone?: string;
    web?: string;
    description?: string;
    email?: string;
    horary?: string;
    type?: string;
    categories?: string[];
    images?: string[];
    //routes?: Route[];
}