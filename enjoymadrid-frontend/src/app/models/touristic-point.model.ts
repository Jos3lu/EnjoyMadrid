import { PointModel } from "./point-model";

export interface TouristicPointModel extends PointModel {
    address?: string;
    zipcode?: number;
    phone?: string;
    description?: string;
    email?: string;
    paymentServices?: string;
    horary?: string;
    type?: string;
    categories?: string[];
    subcategories?: string[];
    images?: string[];
}