export interface TouristicPointModel {
    id?: number;
    longitude?: number;
    latitude?: number;
    name?: string;
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