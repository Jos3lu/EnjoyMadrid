import { Byte } from "@angular/compiler/src/util";

export interface UserModel {
    id?: number;
    name?: string;
    username: string;
    password?: string;
    oldPassword?: string;
    photo?: Byte[];
}