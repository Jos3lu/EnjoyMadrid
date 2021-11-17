import { Component, OnInit, ViewChild } from '@angular/core';
import { IonContent, Platform } from '@ionic/angular';
import { TouristicPointModel } from 'src/app/models/touristic-point.model';
import { SharedService } from 'src/app/services/shared/shared.service';
import { TouristicPointService } from 'src/app/services/touristic-point/touristic-point.service';

@Component({
  selector: 'app-find-places',
  templateUrl: './find-places.page.html',
  styleUrls: ['./find-places.page.scss'],
})
export class FindPlacesPage implements OnInit {

  // Get the content tag
  @ViewChild(IonContent) content: IonContent;

  // List of places to show
  places: TouristicPointModel[];
  // Total of places 
  totalResults: number;
  // Different categories 
  categories: any;
  // To open/close the subcategories of each category
  selectedIndex: number;
  // Show/hide button to  scroll to top
  showScrollTopButton: boolean;

  constructor(
    private touristicPointService: TouristicPointService,
    private sharedService: SharedService,
    private platform: Platform
  ) { }

  ngOnInit() {

    this.places = [];
    this.totalResults = 0;
    this.selectedIndex = -1;
    this.showScrollTopButton = false;

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
          'Spas y balnearios urbanos',
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
          'Compras tradicionales',
          'Gourmet',
          'Moda',
          'Regalo-Hogar-Decoración',
          'Librería',
          'Centros comerciales',
          'Complementos',
          'Moda infantil',
          'Grandes Almacenes',
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
          'Chocolaterías',
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

  onScrolling(event: any) {
    if (event.detail.scrollTop > this.platform.height()) {
      this.showScrollTopButton = true;
    } else {
      this.showScrollTopButton = false;
    }
  }

  scrollToTop() {
    this.content.scrollToTop(500);
  }

  categorySelected(index: number) {
    if (this.selectedIndex == index) {
      this.categories[this.selectedIndex].selected = false;
      this.selectedIndex = -1;
      return;
    } else if (this.selectedIndex > -1) {
      this.categories[this.selectedIndex].selected = false;
    }
    this.selectedIndex = index;
    this.categories[index].selected = true;
  }

  subcategorySelected(subcategory: string) {
    this.touristicPointService.getTouristicPointsByCategory(subcategory).subscribe(
      places => {
        this.places = places;
        this.totalResults = places.length;
        this.content.scrollToPoint(0, document.getElementById('results').offsetTop, 500);
      },
      _ => this.sharedService.showToast('No se ha podido encontrar ningún sitio', 3000)
    );
  }

  placeSelected() {

  }

}
