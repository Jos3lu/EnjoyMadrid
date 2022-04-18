import { Component, OnInit } from '@angular/core';
import * as L from 'leaflet';
import { ModalController, Platform } from '@ionic/angular';
import { trigger, style, animate, transition } from '@angular/animations';
import { SharedService } from 'src/app/services/shared/shared.service';
import { LineString, MultiLineString } from 'geojson';
import { Router } from '@angular/router';
import { RouteResultModel } from 'src/app/models/route-result.model';

@Component({
  selector: 'app-display-route',
  templateUrl: './display-route.page.html',
  styleUrls: ['./display-route.page.scss'],
  animations: [
    // Animation for side menu
    trigger('openClose', [
      transition(':enter', [
        style({ transform: 'translateX(-100%)' }),
        animate('250ms ease-in', style({ transform: 'translateX(0%)' }))
      ]),
      transition(':leave', [
        animate('250ms ease-in', style({ transform: 'translateX(-100%)' }))
      ])
    ])
  ]
})
export class DisplayRoutePage implements OnInit {

  // Get if running on Desktop or mobile
  showSideMenu: boolean;
  // Map
  map: L.Map;
  // Close side menu when user click button
  isOpen: boolean;
  // Route to view in the map
  routeResult: RouteResultModel;
  // Segment highlighted & zoomed when instruction is selected
  stepSelected: L.Polyline;
  // Steps/instructions of walk & bike 
  segmentsSteps: Map<number, any>;
  // Store if it's a subway/bus/commuter or walk/bike segment, the icon to display & if line is solid or dotted
  segmentsVisual: any[];
  // Start time
  startTime: Date;
  // End time
  endTime: Date;
  // Origin & destination points of the route
  origin: string;
  destination: string;
  // Distance unit for route
  distanceUnit: string;

  constructor(
    private platform: Platform,
    private sharedService: SharedService,
    private router: Router,
    private modalController: ModalController
  ) { 
    this.showSideMenu = true;
  }

  ngOnInit() {
    // Get width of device to show side menu or sheet modal
    this.platform.ready().then(() => {
      this.showSideMenu = this.platform.width() >= 768;
    });

    // Different icon for the mode of transport
    let iconTransport: Map<string, string> = new Map<string, string>([['A pie', 'walk.png'], ['Metro', 'subway.png'],
    ['Bus', 'bus.png'], ['BiciMAD', 'bicycle.png'], ['Cercanías', 'commuter.png']]);
    // Map with line zones associated to general line
    let linesZone: Map<string, string> = new Map<string, string>([["6-1", "6"], ["6-2", "6"], ["7a", "7"], ["7b", "7"],
    ["9A", "9"], ["9B", "9"], ["10a", "10"], ["10b","10"], ["12-1", "12"], ["12-2", "12"]]);
    // Init segmentsSteps
    this.segmentsSteps = new Map<number, string>();
    // Init segmentsVisual
    this.segmentsVisual = [];

    this.routeResult = this.sharedService.getRoute();
    this.sharedService.setRoute(undefined);
    if (!this.routeResult) {
      this.sharedService.showToast('No se ha podido obtener la ruta', 3000);
      this.router.navigate(['/']);
    }

    // Iterate over the segments of the route
    this.routeResult.segments.forEach((segment, index) => {
      // Transform some lines to unify zones in one line
      if (linesZone.has(segment.line)) {
        segment.line = linesZone.get(segment.line);
      }

      // Insert the steps if applicable
      if (segment.steps && segment.steps.length) {
        // Init list for the steps
        let steps = [];
        segment.steps.forEach((step: string) => {
          // Split way-points & instruction
          let parts = step.split(':', 2);
          // Get the source/target point & the instruction
          let wayPoints = parts[0].split('-', 2);
          let first = wayPoints[0];
          let last = wayPoints[1];
          let instruction = parts[1];
          steps.push({ first: first, last: last, instruction: instruction });
        });
        this.segmentsSteps.set(index, steps);
      }

      // Dash pattern for the segment (for walk segments)
      let dashedSegment = segment.transportMode === 'A pie';
      // Icon (mode of transport) for the segment
      let iconSegment = iconTransport.get(segment.transportMode);
      // Type of transport (has instructions walk/bike or intermediate stations)
      let stepSegment = segment.transportMode === 'A pie' || segment.transportMode === 'BiciMAD';

      // Store some features of the segments
      this.segmentsVisual.push({ dashed: dashedSegment, icon: iconSegment, steps: stepSegment });
    });

    // Get start & end time of route
    this.startTime = new Date();
    this.endTime = new Date(this.startTime.getTime());
    this.endTime.setMinutes(this.startTime.getMinutes() + this.routeResult.duration);
  }

  ionViewDidLeave() {
    // Close sheet modal if open
    if (!this.showSideMenu) this.modalController.dismiss();
  }

  ionViewDidEnter() {
    // For side menu, starts open
    this.isOpen = true;
    // Miles or km
    this.distanceUnit = this.sharedService.getDistanceUnit();

    // OpenStreetMap
    // https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png
    const standard = L.tileLayer('https://{s}.basemaps.cartocdn.com/rastertiles/voyager/{z}/{x}/{y}.png', {
      attribution: '&copy; <a href="https://www.openstreetmap.org/copyright">OpenStreetMap</a> contributors, ' + 
      '&copy; <a href="https://carto.com/attributions#basemaps">CARTO</a>'
    });

    const satellite = L.tileLayer('https://server.arcgisonline.com/ArcGIS/rest/services/World_Imagery/MapServer/tile/{z}/{y}/{x}', {
      attribution: '&copy; <a href="https://www.esri.com/en-us/home"> Esri</a>'
    });

    // Add layers
    const layers = {
      "<span style='cursor: pointer'>Satélite</span>": satellite,
      "<span style='cursor: pointer'>Predeterminado</span>": standard
    };

    // Create map
    this.map = L.map('map', {
      zoomControl: false,
      center: [40.416694, -3.703250],
      zoom: 12,
      layers: [standard, satellite]
    });

    // Configure controls
    L.control.zoom({ position: 'topright' }).addTo(this.map);
    L.control.layers(layers, null, { position: 'topright' }).addTo(this.map);


    let multiPolyline: L.Polyline<LineString | MultiLineString, any>;
    let coordinates = [];
    // Iterate over the segments of the route
    this.routeResult.segments.forEach((segment, index) => {
      // Transform number[][] to LatLng array in polyline
      let coords = segment.polyline.map(coord => new L.LatLng(coord[0], coord[1]));

      // Establish the color for the segment
      let color = segment.color;
      // Dash pattern for the segment (for walk segments)
      let dashArray = this.segmentsVisual[index].dashed ? '1 7' : null;
      // Image to be used as icon
      let iconSegment = this.segmentsVisual[index].icon;

      // Tooltip set line if applicable
      let lineTooltip = segment.line ?
        '<ion-badge mode="md" style="margin-left: 3px; background-color: '
        + color
        + '; color: #ffffff;">'
        + segment.line
        + '</ion-badge>' : '';
      // Tooltip for the polylines that make up the route
      let tooltip =
        '<div style="text-align: center">'
        + '<div style="display: block"><img style="height: 20px; width: 20px; margin: 0px auto;" src="./assets/'
        + iconSegment + '">'
        + lineTooltip
        + '</div>'
        + this.routeResult.points[segment.source].name
        + '<ion-icon style="vertical-align: middle; font-size: 9px; opacity: 0.7" name="chevron-forward-outline"></ion-icon>'
        + this.routeResult.points[segment.target].name
        + '</div>';

      // Add polyline to map
      L.polyline(coords, {
        stroke: true,
        color: color,
        weight: 5,
        lineJoin: 'round',
        lineCap: 'round',
        dashArray: dashArray
      }).bindTooltip(tooltip, {
        sticky: true
      }).addTo(this.map);

      // Add the coordinates of each segment
      coordinates.push.apply(coordinates, coords);

      // Icon for marker
      let icon = L.icon({
        iconUrl: './assets/' + iconSegment,
        iconSize: [20, 20]
      });

      // Add markers to map
      new L.Marker([this.routeResult.points[segment.source].latitude, this.routeResult.points[segment.source].longitude], {
        icon: icon
      }).addTo(this.map).bindTooltip(this.routeResult.points[segment.source].name);

      // Walk polyline for stops with transfer between lines
      if (index > 0 && segment.transportMode === this.routeResult.segments[index - 1].transportMode) {
        let previous = this.routeResult.segments[index - 1].polyline[this.routeResult.segments[index - 1].polyline.length - 1];
        let current = segment.polyline[0];
        coords = [new L.LatLng(previous[0], previous[1]), new L.LatLng(current[0], current[1])];
        // Add polyline to map
        L.polyline(coords, {
          stroke: true,
          color: '#2D2E2D',
          weight: 5,
          lineJoin: 'round',
          lineCap: 'round',
          dashArray: '1 7'
        }).addTo(this.map);

        // Add the coordinates of each segment
        coordinates.push.apply(coordinates, coords);
      }
    });

    let destinationPoint = this.routeResult.points[this.routeResult.points.length - 1];
    // Marker for destination point
    new L.Marker([destinationPoint.latitude, destinationPoint.longitude], {
      icon: L.icon({ iconUrl: './assets/destination.png', iconSize: [25, 25] })
    }).addTo(this.map).bindTooltip(destinationPoint.name);

    // Set names of start & end points of the route
    this.origin = this.routeResult.points[0].name;
    this.destination = destinationPoint.name;

    // Polyline of the whole route
    multiPolyline = L.polyline(coordinates);
    this.map.fitBounds(multiPolyline.getBounds(), { paddingTopLeft: [this.showSideMenu ? 300 : 0, 0] });

    // Remove highlighted polyline if 
    this.map.on('zoomend', () => this.closeStep());
  }

  // Close/open sidemenu
  toggleMenu() {
    this.isOpen = !this.isOpen;
  }

  // Format time
  formatTime(date: Date) {
    return date.toLocaleTimeString(navigator.language, {hour: '2-digit', minute: '2-digit'})
  }

  // Format duration of segment/route
  formatDuration(duration: number) {
    return (duration < 60 ? duration : Math.floor(duration / 60) + ' h ' + duration % 60) + ' min';
  }

  // When step in walk or bike is selected
  zoomToStep(first: number, last: number, index: number) {
    // If there is already a polyline highlighted remove it from the map
    if (this.stepSelected) {
      this.map.removeLayer(this.stepSelected);
    }

    // Get coordinates to highlight & zoom
    let polyline = this.routeResult.segments[index].polyline.slice(first, ++last)
      .map(coord => new L.LatLng(coord[0], coord[1]));;

    // Add polyline to map
    this.stepSelected = L.polyline(polyline, {
      stroke: true,
      color: '#2D2E2D',
      weight: 5,
      lineJoin: 'round',
      lineCap: 'round'
    }).addTo(this.map);
    // Zoom to polyline
    this.map.fitBounds(this.stepSelected.getBounds(), { paddingTopLeft: [this.showSideMenu ? 150 : 0, 0] });
  }

  // Remove polyline highlighted from map
  closeStep() {
    // Already polyline & zoom has to be lower than 16
    if (this.stepSelected && this.map.getZoom() <= 15) {
      this.map.removeLayer(this.stepSelected);
      this.stepSelected = undefined;
    }
  }

  // Check if there are intermediate stops in a segment route
  checkIntermediateStops(segment: any) {
    return (segment.target - segment.source) > 1;
  }

  // Return names of the intermediate stops 
  intermediateStopsNames(start: number, end: number) {
    let names = [];
    for (let i = start + 1; i < end; i++) {
      names.push(this.routeResult.points[i].name);
    }
    return names;
  }

}
