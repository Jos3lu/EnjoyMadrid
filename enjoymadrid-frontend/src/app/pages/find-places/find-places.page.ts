import { Component, OnInit, ViewChild } from '@angular/core';
import { SafeHtml } from '@angular/platform-browser';
import { IonContent, IonInfiniteScroll, ModalController, Platform } from '@ionic/angular';
import { TouristicPointModel } from 'src/app/models/touristic-point.model';
import { SharedService } from 'src/app/services/shared/shared.service';
import { TouristicPointService } from 'src/app/services/touristic-point/touristic-point.service';
import { InfoPlacePage } from '../info-place/info-place.page';

@Component({
  selector: 'app-find-places',
  templateUrl: './find-places.page.html',
  styleUrls: ['./find-places.page.scss'],
})
export class FindPlacesPage implements OnInit {

  // Get the content & ion-infinite tag
  @ViewChild(IonInfiniteScroll) infiniteScroll: IonInfiniteScroll;
  @ViewChild(IonContent) content: IonContent;

  // List of places to show
  places: TouristicPointModel[];
  // Number of elements to show
  lastIndex: number;
  // Total of places 
  totalResults: number;
  // Different categories 
  categories: any;
  // To open/close the subcategories of each category
  selectedIndex: number;
  // Show/hide button to  scroll to top
  showScrollTopButton: boolean;

  // Search query
  searchQuery: string;

  constructor(
    private touristicPointService: TouristicPointService,
    private sharedService: SharedService,
    private platform: Platform,
    private modalContrall: ModalController
  ) { }

  ngOnInit() {

    this.places = [];
    this.lastIndex = 0;
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

  search() {
    // Process query
    console.log(this.searchQuery);
  }

  loadData(event: any) {
    // Load 10 touristic points when bottom page reached
    if (this.lastIndex + 10 > this.places.length) {
      this.lastIndex = this.places.length;
      event.target.complete();
      event.target.disabled = true;
      return;
    } 
    this.lastIndex += 10;
    event.target.complete();
  }

  onScrolling(event: any) {
    // Get if bottom page reached
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
    // Open subcategories 
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
    // Get touristic points associated to category & subcategory
    this.touristicPointService.getTouristicPointsByCategory(subcategory).subscribe(
      places => {
        this.places = places;
        this.lastIndex = 10;
        this.totalResults = places.length;
        this.infiniteScroll.disabled = false;
        this.content.scrollToPoint(0, document.getElementById('results').offsetTop, 500);
      },
      error => this.sharedService.onError(error, 3000)
    );
  }

  async placeSelected(index: number) {
    // Open modal with point information
    const modal = await this.modalContrall.create({
      cssClass: 'my-modal',
      component: InfoPlacePage,
      componentProps: {
        'place': this.places[index]
      }
    });
    await modal.present();
  }

  async onError(event: any) {
    // Reload image if error loading it
    this.sharedService.reloadImage(event, 'data-retry', 'data-max-retry', 'assets/flag.png');
  }

  sanitizeHtml(innerHTMl: string): SafeHtml {
    // Sanitize html
    return this.sharedService.sanitizeHtml(innerHTMl);
  }

}
