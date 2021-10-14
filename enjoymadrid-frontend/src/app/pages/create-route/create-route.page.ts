import { Component, OnInit } from '@angular/core';

@Component({
  selector: 'app-create-route',
  templateUrl: './create-route.page.html',
  styleUrls: ['./create-route.page.scss'],
})
export class CreateRoutePage implements OnInit {

  cultureSelected: boolean;
  gardensSelected: boolean;
  guideCompaniesSelected: boolean;
  monumentsSelected: boolean;
  eventsSelected: boolean;
  leisureCentersSelected: boolean;
  sportSelected: boolean;
  storesSelected: boolean;
  clubsSelected: boolean;
  restaurantsSelected: boolean;
  othersSelected: boolean;
  
  cultureList: any[];
  sportsList: any[];
  storesList: any[];
  clubsList: any[];
  restaurantsList: any[];

  maxPoints: number;

  constructor() { }

  ngOnInit() {

    this.cultureSelected = false;
    this.gardensSelected = false;
    this.guideCompaniesSelected = false;
    this.monumentsSelected = false;
    this.eventsSelected = false;
    this.leisureCentersSelected = false;
    this.sportSelected = false;
    this.storesSelected = false;
    this.clubsSelected = false;
    this.restaurantsSelected = false;
    this.othersSelected = false;

    this.cultureList = [{value: "Teatros", selected: false}, {value: "Galerías de arte", selected: false}, 
    {value: "Centros culturales / Salas de exposiciones / Fundaciones", selected: false}, {value: "Museos", selected: false},
    {value: "Cines", selected: false}, {value: "Multiespacio", selected: false}, {value: "Salas de música y conciertos", selected: false}];

    this.sportsList = [{value: "Instalaciones y centros deportivos", selected: false}, {value: "Gimnasios", selected: false},
    {value: "Spas y balnearios urbanos", selected: false}, {value: "Alquiler de bicicletas", selected: false}, 
    {value: "Campos de golf", selected: false}, {value: "Piscinas", selected: false}, {value: "Pistas de hielo", selected: false}];

    this.clubsList = [{value: "Discoteca", selected: false}, {value: "Musica directo", selected: false}, {value: "Cafés", selected: false},
    {value: "Terrazas", selected: false}, {value: "Flamenco", selected: false}, {value: "Bar de copas", selected: false}, 
    {value: "Bingos-casinos", selected: false}, {value: "Karaokes", selected: false}, {value: "Bares", selected: false}, 
    {value: "Coctelerías", selected: false}, {value: "Chocolaterías", selected: false}, {value: "Otros", selected: false}];

    this.storesList = [{value: "Artesanía", selected: false}, {value: "Joyería", selected: false}, {value: "Zapatería", selected: false}, 
    {value: "Deporte", selected: false}, {value: "Compras tradicionales", selected: false}, {value: "Gourmet", selected: false}, 
    {value: "Moda", selected: false}, {value: "Regalo-Hogar-Decoración", selected: false}, {value: "Librería", selected: false}, 
    {value: "Centros comerciales", selected: false}, {value: "Complementos", selected: false}, {value: "Moda infantil", selected: false}, 
    {value: "Grandes almacenes", selected: false}, {value: "Música", selected: false}, {value: "Tecnología", selected: false}, 
    {value: "Heladerías", selected: false}, {value: "Pastelerías", selected: false}, {value: "Jugueterías", selected: false}, 
    {value: "Mercados", selected: false}, {value: "Floristerías", selected: false}, {value: "Anticuarios", selected: false}, 
    {value: "Perfumerías-Belleza", selected: false}, {value: "Otros", selected: false}];

    this.restaurantsList = [{value: "Internacional", selected: false}, {value: "Española", selected: false}, {value: "De autor", selected: false}, 
    {value: "Especiales", selected: false}, {value: "Bares", selected: false}, {value: "Vegano", selected: false}, 
    {value: "Vegetariano", selected: false}, {value: "Tapas", selected: false}, {value: "Tabernas", selected: false}];

    this.maxPoints = 50;

  }

}
