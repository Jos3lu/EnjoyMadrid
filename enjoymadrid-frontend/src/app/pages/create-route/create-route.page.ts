import { Component, OnInit } from '@angular/core';
import { RouteModel } from 'src/app/models/route.model';
import { RouteService } from 'src/app/services/route/route.service';
import { SharedService } from 'src/app/services/shared/shared.service';

@Component({
  selector: 'app-create-route',
  templateUrl: './create-route.page.html',
  styleUrls: ['./create-route.page.scss'],
})
export class CreateRoutePage implements OnInit {

  route: RouteModel;
  preferences: any;
  nameRoute: string;
  maxPoints: number;

  constructor(
    private sharedService: SharedService,
    private routeService: RouteService
  ) { }

  ngOnInit() {
    this.initRoute();
  }

  initRoute() {

    this.route = { name: ''}
    this.maxPoints = 50;

    this.preferences = [
      {
        value: 'Cultura y arte', 
        selected: false,
        subpreferences: [
          { value: "Teatros", selected: false }, 
          { value: "Galerías de arte", selected: false }, 
          { value: "Centros culturales / Salas de exposiciones / Fundaciones", selected: false }, 
          { value: "Museos", selected: false },
          { value: "Cines", selected: false }, 
          { value: "Multiespacio", selected: false }, 
          { value: "Salas de música y conciertos", selected: false }
        ]
      },
      {
        value: 'Parques y jardines',
        selected: false
      },
      {
        value: 'Empresas de guías Turísticas',
        selected: false
      },
      {
        value: 'Edificios y monumentos',
        selected: false
      },
      {
        value: 'Espacios para eventos',
        selected: false
      },
      {
        value: 'Centros de ocio',
        selected: false
      },
      {
        value: 'Deportes',
        selected: false,
        subpreferences: [
          { value: "Instalaciones deportivas", selected: false }, 
          { value: 'Centros deporitvos', selected: false },
          { value: "Gimnasios", selected: false },
          { value: "Spas y balnearios urbanos", selected: false }, 
          { value: "Alquiler de bicicletas", selected: false }, 
          { value: "Campos de golf", selected: false }, 
          { value: "Piscinas", selected: false }, 
          { value: "Pistas de hielo", selected: false }
        ]
      },
      {
        value: 'Tiendas',
        selected: false,
        subpreferences: [
          { value: "Artesanía", selected: false }, 
          { value: "Joyería", selected: false }, 
          { value: "Zapatería", selected: false }, 
          { value: "Deporte", selected: false }, 
          { value: "Compras tradicionales", selected: false }, 
          { value: "Gourmet", selected: false }, 
          { value: "Moda", selected: false }, 
          { value: "Regalo-Hogar-Decoración", selected: false }, 
          { value: "Librería", selected: false }, 
          { value: "Centros comerciales", selected: false }, 
          { value: "Complementos", selected: false }, 
          { value: "Moda infantil", selected: false }, 
          { value: "Grandes almacenes", selected: false }, 
          { value: "Música", selected: false }, 
          { value: "Tecnología", selected: false }, 
          { value: "Heladerías", selected: false }, 
          { value: "Pastelerías", selected: false }, 
          { value: "Jugueterías", selected: false }, 
          { value: "Mercados", selected: false }, 
          { value: "Floristerías", selected: false }, 
          { value: "Anticuarios", selected: false }, 
          { value: "Perfumerías-Belleza", selected: false }, 
          { value: "Otros", selected: false }
        ]
      },
      {
        value: 'Hostelería y noche',
        selected: false,
        subpreferences: [
          { value: "Discoteca", selected: false }, 
          { value: "Musica directo", selected: false }, 
          { value: "Cafés", selected: false },
          { value: "Terrazas", selected: false }, 
          { value: "Flamenco", selected: false }, 
          { value: "Bar de copas", selected: false }, 
          { value: "Bingos-casinos", selected: false }, 
          { value: "Karaokes", selected: false }, 
          { value: "Bares", selected: false }, 
          { value: "Coctelerías", selected: false }, 
          { value: "Chocolaterías", selected: false }, 
          { value: "Otros", selected: false }
        ]
      },
      {
        value: 'Restaurantes',
        selected: false,
        subpreferences: [
          {value: "Internacional", selected: false}, {value: "Española", selected: false}, {value: "De autor", selected: false}, 
          {value: "Especiales", selected: false}, {value: "Bares", selected: false}, {value: "Vegano", selected: false}, 
          {value: "Vegetariano", selected: false}, {value: "Tapas", selected: false}, {value: "Tabernas", selected: false}
        ]
      },
      {
        value: 'Otros',
        selected: false
      }
    ];

  }

  onCreateRoute() {
    let listPreferences: any = [];

    for (let preference of this.preferences) {
      if (preference.selected) {
        let listSubpreferences: string[] = [];
        if (preference.subpreferences) {
          listSubpreferences = preference.subpreferences.reduce((reducedSubpreferences: string[], subpreference: any) => 
                subpreference.selected ? reducedSubpreferences.concat(subpreference.value) : reducedSubpreferences, []);

          if (!listSubpreferences.length) {
            this.sharedService.showToast('Al seleccionar "' + preference.value + '" te ha faltado elegir alguna subcategoría', 5000);
            return;
          }
        } 

        // Add each preference (and the subpreferences)
        listPreferences.push({ category: preference.value, subcategories: listSubpreferences });
      }
    }

    if (!listPreferences.length) {
      this.sharedService.showToast('Seguro que tienes alguna preferencia, selecciona al menos una y ya se podrá crear la ruta', 5000);
      return;
    }

    this.route.preferences = listPreferences;
    this.route.maxPoints = this.maxPoints;

    this.routeService.createRoute(this.route).subscribe(
      (points: any) => {
        console.log(points);
      }
    );
  }

}
