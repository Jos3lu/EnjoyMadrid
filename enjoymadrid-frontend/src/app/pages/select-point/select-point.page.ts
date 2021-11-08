import { Component, Input, OnInit } from '@angular/core';
import { RouteModel } from 'src/app/models/route.model';
import { GeoSearchControl, OpenStreetMapProvider, SearchControl } from 'leaflet-geosearch';
import * as L from 'leaflet';

@Component({
  selector: 'app-select-point',
  templateUrl: './select-point.page.html',
  styleUrls: ['./select-point.page.scss'],
})
export class SelectPointPage implements OnInit {

  provider = new OpenStreetMapProvider();
  map: L.Map | L.LayerGroup<any>;

  constructor() { }

  ngOnInit() {
  }

  ionViewDidEnter() {

    const standard = L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
      attribution: 'Imagery &copy; <a href="https://www.openstreetmap.org/copyright">OpenStreetMap</a> contributors'
    });

    const satellite = L.tileLayer('https://server.arcgisonline.com/ArcGIS/rest/services/World_Imagery/MapServer/tile/{z}/{y}/{x}', {
      attribution: 'Imagery &copy; Esri'
    });

    const layers = {
      "Satélite": satellite,
      "Predeterminado": standard
    };

    const searchControl = GeoSearchControl({
      provider: new OpenStreetMapProvider(),
      style: 'bar',
      autoCompleteDelay: 500,
      showMarker: false,
      searchLabel: 'Buscar o clicar mapa'
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

  async searchPoint(result: any) {
    //L.marker([result.location.x, result.location.y]).addTo(this.map);
    L.marker([40.41379255, -3.6920378829351304]).addTo(this.map);
    console.log(result);
  }

  selectPoint(result: any) {
    console.log(result);
    //const address = await this.provider.reverseUrl
    L.marker([result.latlng.lat, result.latlng.lng]).addTo(this.map);
  }

  onSelect() {

  }

}
