import { Component, OnInit } from '@angular/core';

@Component({
  selector: 'app-find-places',
  templateUrl: './find-places.page.html',
  styleUrls: ['./find-places.page.scss'],
})
export class FindPlacesPage implements OnInit {

  categories: any;
  selectedIndex: number;

  constructor() { }

  ngOnInit() {

    this.selectedIndex = -1;

    this.categories = [
      {
        name: 'Turístico',
        selected: false,
        subcategories: [
          'Escuelas de cocina y catas de vinos y aceites',
          'Instalaciones culturales',
          'Parques y jardines',
          'Empresas de guías turísticos',
          'Edificios y monumentos',
          'Espacios para eventos',
          'Servicios',
          'Parques y centros de ocio',
          'Instalaciones deportivas'
        ]
      },
      {
        name: 'Deportes',
        selected: false,
        subcategories: [
          'Centros deportivos',
          'Gimnasios',
          'Spas y balneraios urbanos',
          'Alquiler de bicicletas',
          'Golf',
          'Piscinas',
          'Pistas de hielo'
        ]
      },
      {
        name: 'Tiendas',
        selected: false,
        subcategories: [
          'Artesanía',
          'Joyerías',
          'Zapaterías',
          'Deporte',
          'Compras tradidionales',
          'Gourmet',
          'Moda',
          'Regalo-Hogar-Decoración',
          'Librería',
          'Centros comerciales',
          'Complementos',
          'Moda infantil',
          'Grandes almacenes',
          'Música',
          'Tecnología',
          'Heladerías',
          'Pastelerías',
          'Jugueterías',
          'Mercados',
          'Floristerías',
          'Anticuarios',
          'Perfumerías-Belleza',
          'Otros'
        ]
      },
      {
        name: 'Ocio',
        selected: false,
        subcategories: [
          'Discoteca',
          'Musica directo',
          'Cafés',
          'Terrazas',
          'Flamenco',
          'Bar de copas',
          'Bingos - Casinos',
          'Karaokes',
          'Bares',
          'Coctelerías',
          'Chocalaterías',
          'Otros'
        ]
      },
      {
        name: 'Restaurantes',
        selected: false,
        subcategories: [
          'Internacional',
          'Española',
          'De autor',
          'Especiales',
          'Bares',
          'Vegano',
          'Vegetariano',
          'Tapas',
          'Multiespacio',
          'Tabernas'
        ]
      }
    ];

  }

  categorySelected(i: number) {
    if (this.selectedIndex == i) {
      this.categories[this.selectedIndex].selected = false;
      this.selectedIndex = -1;
      return;
    } else if (this.selectedIndex > -1) {
      this.categories[this.selectedIndex].selected = false;
    }
    this.selectedIndex = i;
    this.categories[i].selected = true;
  }

}
