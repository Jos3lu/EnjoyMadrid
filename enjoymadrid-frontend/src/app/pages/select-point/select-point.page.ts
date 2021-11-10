import { Component, Input, OnInit } from '@angular/core';
import { GeoSearchControl, OpenStreetMapProvider } from 'leaflet-geosearch';
import { Geolocation } from '@ionic-native/geolocation/ngx';
import * as L from 'leaflet';
import { SharedService } from 'src/app/services/shared/shared.service';
import { ModalController } from '@ionic/angular';
import { Router } from '@angular/router';

@Component({
  selector: 'app-select-point',
  templateUrl: './select-point.page.html',
  styleUrls: ['./select-point.page.scss'],
})
export class SelectPointPage implements OnInit {

  @Input() isOrigin: boolean;

  provider = new OpenStreetMapProvider();
  map: L.Map;
  marker: L.Marker; 
  point: any; 

  constructor(
    private geolocation: Geolocation,
    private sharedService: SharedService,
    private modalController: ModalController,
    private router: Router
  ) { }

  ngOnInit() {
  }

  ionViewDidEnter() {

    const standard = L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
      attribution: '&copy; <a href="https://www.openstreetmap.org/copyright">OpenStreetMap</a> contributors'
    });

    const satellite = L.tileLayer('https://server.arcgisonline.com/ArcGIS/rest/services/World_Imagery/MapServer/tile/{z}/{y}/{x}', {
      attribution: '&copy; Esri'
    });

    const layers = {
      "<span style='cursor: pointer'>Satélite</span>": satellite,
      "<span style='cursor: pointer'>Predeterminado</span>": standard
    };

    const searchControl = GeoSearchControl({
      provider: new OpenStreetMapProvider(),
      style: 'bar',
      autoCompleteDelay: 500,
      showMarker: false,
      retainZoomLevel: true,
      searchLabel: 'Buscar o clicar mapa',
      notFoundMessage: 'No se ha podido encontrar la dirección',
    });

    this.map = L.map('map', {
      zoomControl: false,
      center: [40.416694, -3.703250],
      zoom: 12,
      layers: [standard, satellite]
    });

    this.map.addControl(searchControl);
    L.control.zoom({ position: 'topright' }).addTo(this.map);
    L.control.layers(layers, null, { position: 'topright' }).addTo(this.map);

    this.map.on('geosearch/showlocation', e => this.searchPoint(e));
    this.map.on('click', e => this.selectPoint(e));

  }

  searchPoint(result: any) {
    this.setMarker(result.location.y, result.location.x, result.location.label);
  }

  selectPoint(result: any) {
    this.setMarker(result.latlng.lat, result.latlng.lng, 'Ubicación seleccionada');
  }

  searchCurrentLocation() {
    this.geolocation.getCurrentPosition().then(position => {
      this.setMarker(position.coords.latitude, position.coords.longitude, 'Tu ubicación');
    }).catch(error => {
      this.sharedService.showToast('No se ha podido obtener la localización', 3000);
      console.log(error);
    });
  }

  setMarker(latitude: number, longitude: number, location: string) {
    this.point = { latitude: latitude, longitude: longitude, location: location };
    if(this.marker) {
      this.map.removeLayer(this.marker); 
    }
    this.marker = L.marker([latitude, longitude]);
    this.marker.addTo(this.map);
    this.map.flyTo([latitude, longitude], 18);
  }

  onSelect() {
    if (!this.point) {
      this.sharedService.showToast('Selecciona una localización', 3000);
      return;
    }

    this.modalController.dismiss({
      'point': this.point
    });
    this.router.navigateByUrl('/create-route');
  }

}