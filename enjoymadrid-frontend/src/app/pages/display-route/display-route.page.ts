import { Component, OnInit } from '@angular/core';
import * as L from 'leaflet';
import { Platform } from '@ionic/angular';
import { trigger, style, animate, transition } from '@angular/animations';
import { RouteModel } from 'src/app/models/route.model';
import { SharedService } from 'src/app/services/shared/shared.service';
import { Router } from '@angular/router';
import { LineString, MultiLineString } from 'geojson';

@Component({
  selector: 'app-display-route',
  templateUrl: './display-route.page.html',
  styleUrls: ['./display-route.page.scss'],
  animations: [
    trigger('openClose', [
      transition(':enter', [
        style({transform: 'translateX(-100%)'}),
        animate('250ms ease-in', style({transform: 'translateX(0%)'}))
      ]),
      transition(':leave', [
        animate('250ms ease-in', style({transform: 'translateX(-100%)'}))
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
  route: RouteModel
  // Segment highlighted & zoomed when instruction is selected
  stepSelected: L.Polyline;
  // Steps/instructions of walk & bike 
  segmentsSteps: Map<number, any>;
  // Store the segment color, the icon to display & if line is solid or dotted
  segmentsVisual: any[];

  constructor(
    private platform: Platform,
    private sharedService: SharedService,
    private router: Router
    ) 
  { }

  ngOnInit() {
    this.platform.ready().then(() => {
      this.showSideMenu = this.platform.is('desktop');
    });

    // Different icon for the mode of transport
    let iconTransport: Map<string, string> = new Map<string, string>([ ['A pie', 'walk.png'], ['Metro', 'subway.png'], 
      ['Bus', 'bus.png'], ['BiciMAD', 'bicycle.png'], ['Cercanías', 'commuter.png'] ]);
    // Map with line zones associated to general line
    let linesZone: Map<string, string> = new Map<string, string>([ ["6-1", "6"], ["6-2", "6"], ["7a", "7"], ["7b", "7"], 
      ["9A", "9"], ["9B", "9"], ["12-1", "12"], ["12-2", "12"] ]);
    // Map with the color of the different lines of subway & commuter. Also associate color to walk, bus & bike
    let colorSegments: Map<string, string> = new Map<string, string>([ ["1", "#2DBEF0"], ["2", "#ED1C24"], ["3", "#FFD000"], 
      ["4", "#B65518"], ["5", "#8FD400"], ["6", "#98989B"], ["7", "#EE7518"], ["8", "#EC82B1"], 
      ["9", "#A60084"], ["10", "#005AA9"], ["11", "#009B3A"], ["12", "#A49800"], ["R", "#D5D2CD"],
      ["C-1", "#4FB0E5"], ["C-2", "#008B45"], ["C-3", "#9F2E86"], ["C-3a", "#EA65A8"], ["C-4a", "#005AA3"], ["C-4b", "#005AA3"], 
      ["C-5", "F9BA13#"], ["C-7", "#ED1C24"], ["C-8", "#008B45"], ["C-9", "#F95900"], ["C-10", "#90B70E"], 
      ["Bus", "#0178BC"], ["BiciMAD","#FFAD00"], ["A pie","#2D2E2D"] ]);
    // Init segmentsSteps
    this.segmentsSteps = new Map<number, string>();
    // Init segmentsVisual
    this.segmentsVisual = [];

    this.route = this.sharedService.getRoute();
    this.sharedService.setRoute(undefined);
    if (!this.route) {
      this.route = {
          id: 8516, 
          name: "Ruta",
          origin: {id: 8518,name: "40.42286544873439, -3.5610637685749684",longitude: -3.5610637685749684,latitude: 40.42286544873439},
          destination: {id: 8517,name: "40.41631895325964, -3.711318969726563",longitude: -3.711318969726563,latitude: 40.41631895325964},
          maxDistance: 1,
          transports: ["Metro","Bus","Cercanías","BiciMAD"],
          preferences: {"C_Instalaciones culturales": 5,"C_Parques y jardines": 3,"C_Escuelas de cocina y catas de vinos y aceites": 0,"C_Empresas de guías Turísticas": 0,"C_Edificios y monumentos": 5,"C_Centros de ocio": 0,"D_Deportes": 0,"T_Tiendas": 3,"R_Restauración": 2},
          date: new Date("2022-02-12"),
          points: [{id: 8518,name: "40.42286544873439, -3.5610637685749684",longitude: -3.5610637685749684,latitude: 40.42286544873439},{id: 3796,name: "Barrio del puerto",longitude: -3.56918847026566,latitude: 40.422496870096},{id: 3687,name: "Estadio metropolitano",longitude: -3.60015365421252,latitude: 40.4333934840999},{id: 3379,name: "Las musas",longitude: -3.60787951427632,latitude: 40.4329881279398},{id: 3614,name: "San blas",longitude: -3.61546907625776,latitude: 40.4279919067674},{id: 8195,name: "Castillo ucles-castillo simancas",longitude: -3.62311936906799,latitude: 40.43017307797765},{id: 6252,name: "Castillo ucles nº 49",longitude: -3.623000369495517,latitude: 40.43249182553872},{id: 6314,name: "San romualdo-albasanz",longitude: -3.621539737064135,latitude: 40.43881250747026},{id: 4538,name: "San romualdo-alcala",longitude: -3.623098382987294,latitude: 40.44161496672764},{id: 5551,name: "Alcala-alegria de oria",longitude: -3.630852353515179,latitude: 40.43994027855287},{id: 5545,name: "Alcala-riobamba",longitude: -3.632932960235741,latitude: 40.4393584073009},{id: 7853,name: "Alcala-boldano",longitude: -3.640659918211028,latitude: 40.43683548055043},{id: 4350,name: "Alcala-hermanos de pablo",longitude: -3.644025526331181,latitude: 40.435176145383764},{id: 8271,name: "Alcala-virgen del sagrario",longitude: -3.64796307730134,latitude: 40.43330488204536},{id: 8011,name: "Alcala-florencio llorente",longitude: -3.652906837243782,latitude: 40.43255835887936},{id: 8241,name: "Roberto domingo - alcala",longitude: -3.661586283954598,latitude: 40.431973072808255},{id: 5338,name: "Fco.altimiras-av.los toreros",longitude: -3.663666764691055,latitude: 40.43385805698138},{id: 3544,name: "Ortega y Gasset 87",longitude: -3.6712823,latitude: 40.429887},{id: 3608,name: "General Pardiñas",longitude: -3.6782777,latitude: 40.4290555},{id: 3385,name: "Nuñez de balboa",longitude: -3.67936371945562,latitude: 40.4301298212657},{id: 3841,name: "Ruben dario",longitude: -3.68954159857549,latitude: 40.4331579372096},{id: 3315,name: "Alonso martinez",longitude: -3.69643188600389,latitude: 40.42852602883},{id: 3793,name: "Chueca",longitude: -3.69761659244378,latitude: 40.4229331780798},{id: 3329,name: "Gran via",longitude: -3.70180393013566,latitude: 40.4199866177357},{id: 3478,name: "Sol",longitude: -3.70326164857528,latitude: 40.416876060894},{id: 8517,name: "40.41631895325964, -3.711318969726563",longitude: -3.711318969726563,latitude: 40.41631895325964}],
          segments: [
            {id: 8519,source: 0,target: 1,distance: 0.86,duration: 10,transportMode: "A pie",steps: ["0-2:Camina hacia el suroeste","2-3:¡Gire a la derecha!","3-4:¡Gire a la izquierda!","4-5:¡Gire bastante a la derecha!","5-9:¡Gire bastante a la izquierda!","9-11:¡Gire a la derecha!","11-16:¡Gire a la derecha!","16-17:¡Gire a la izquierda!","17-19:¡Gire ligeramente a la izquierda!","19-21:¡Gire ligeramente a la izquierda!","21-27:¡Gire a la izquierda!","27-32:¡Gire a la izquierda!","32-39:¡Gire a la derecha!","39-44:¡Gire a la derecha!","44-51:¡Gire a la derecha!","51-52:¡Gire a la izquierda!","52-52:Llegar a su destino, a la derecha"],"polyline": [[40.422886,-3.561115],[40.422881,-3.561118],[40.422591,-3.561308],[40.422647,-3.561465],[40.422594,-3.561498],[40.422641,-3.561535],[40.422266,-3.561791],[40.421837,-3.562107],[40.421646,-3.562341],[40.421585,-3.562396],[40.421571,-3.562409],[40.421363,-3.562612],[40.421385,-3.56273],[40.421466,-3.563186],[40.421502,-3.563227],[40.421522,-3.563234],[40.421555,-3.56324],[40.42157,-3.563271],[40.421568,-3.563313],[40.421589,-3.563337],[40.4216,-3.563416],[40.421619,-3.563543],[40.421582,-3.563579],[40.421547,-3.563718],[40.421548,-3.563732],[40.421558,-3.563785],[40.421616,-3.564108],[40.42178,-3.564779],[40.421735,-3.564802],[40.421675,-3.564832],[40.421645,-3.564847],[40.421634,-3.564853],[40.421614,-3.564863],[40.421792,-3.565566],[40.421997,-3.566345],[40.42202,-3.566432],[40.422028,-3.566484],[40.422012,-3.566528],[40.421981,-3.566558],[40.421937,-3.566575],[40.421945,-3.566612],[40.421955,-3.566654],[40.421963,-3.566689],[40.42197,-3.566716],[40.421984,-3.566773],[40.422008,-3.566773],[40.422033,-3.566771],[40.422117,-3.56684],[40.422138,-3.566897],[40.422164,-3.567001],[40.422491,-3.568312],[40.422651,-3.568941],[40.422456,-3.569024]],line: null},
            {id: 8520,source: 1,target: 2,distance: null,duration: null,transportMode: "Metro",steps: [],"polyline": [[40.422496870096,-3.56918847026566],[40.4333934840999,-3.60015365421252]],line: "7b"},
            {id: 8521,source: 2,target: 4,distance: null,duration: null,transportMode: "Metro",steps: [],"polyline": [[40.4333934840999,-3.60015365421252],[40.4329881279398,-3.60787951427632],[40.4279919067674,-3.61546907625776]],line: "7a"},
            {id: 8522,source: 4,target: 5,distance: 0.96,duration: 12,transportMode: "A pie",steps: ["0-11:Camina hacia el este","11-12:¡Gire a la derecha!","12-21:¡Gire a la izquierda a Calle de Alberique!","21-22:¡Gire bastante a la derecha a Calle de Albaida!","22-25:¡Gire bastante a la izquierda!","25-26:¡Gire a la derecha!","26-29:¡Gire a la izquierda!","29-31:¡Gire ligeramente a la derecha!","31-41:¡Gire ligeramente a la izquierda!","41-43:¡Gire a la derecha!","43-44:¡Gire a la izquierda!","44-46:¡Gire a la izquierda!","46-47:¡Gire a la derecha!","47-47:Llegar a su destino, a la derecha"],"polyline": [[40.42796,-3.615453],[40.428019,-3.615258],[40.428045,-3.615226],[40.428118,-3.615237],[40.428315,-3.61527],[40.42871,-3.615412],[40.428998,-3.615512],[40.429444,-3.615665],[40.429461,-3.615692],[40.429459,-3.615759],[40.429384,-3.616566],[40.429355,-3.616884],[40.429426,-3.616897],[40.429414,-3.617016],[40.429373,-3.617442],[40.429349,-3.617677],[40.429332,-3.617859],[40.429263,-3.618581],[40.429225,-3.618976],[40.429129,-3.619968],[40.429096,-3.620195],[40.429073,-3.620316],[40.429186,-3.620306],[40.429166,-3.620412],[40.429137,-3.620478],[40.429037,-3.620695],[40.429252,-3.620803],[40.429265,-3.620898],[40.42924,-3.620988],[40.429122,-3.621417],[40.429201,-3.621683],[40.42929,-3.621909],[40.429288,-3.622244],[40.429261,-3.622446],[40.42926,-3.622537],[40.429255,-3.622713],[40.429252,-3.622822],[40.429271,-3.622884],[40.429326,-3.622942],[40.429419,-3.622997],[40.429587,-3.62306],[40.429645,-3.62312],[40.429802,-3.623086],[40.429874,-3.623057],[40.429908,-3.623046],[40.43003,-3.623166],[40.43004,-3.623221],[40.430183,-3.623184]],line: null},
            {id: 8523,source: 5,target: 6,distance: null,duration: null,transportMode: "Bus",steps: [],"polyline": [[40.430198,-3.623273],[40.430285,-3.623248],[40.430619,-3.623166],[40.430767,-3.623134],[40.431369,-3.622983],[40.431793,-3.622869],[40.431824,-3.623058],[40.432476,-3.622891]],line: "109"},
            {id: 8524,source: 6,target: 7,distance: 0.92,duration: 11,transportMode: "A pie",steps: ["0-4:Camina hacia el norte","4-5:¡Gire a la derecha!","5-9:¡Gire bastante a la izquierda a Calle Castillo de Uclés!","9-12:¡Gire a la derecha a Calle de Emilio Muñoz!","12-16:¡Gire a la izquierda a Calle de Miguel Yuste!","16-21:¡Gire a la derecha a Calle de Julián Camarillo!","21-22:¡Gire a la izquierda a Calle de San Romualdo!","22-24:¡Gire a la derecha!","24-24:Llegar a su destino, a la izquierda"],"polyline": [[40.432494,-3.623016],[40.432643,-3.622983],[40.432689,-3.622881],[40.432764,-3.622699],[40.433402,-3.622539],[40.433389,-3.622462],[40.433446,-3.622466],[40.433532,-3.622498],[40.433631,-3.62258],[40.434282,-3.623071],[40.434437,-3.622787],[40.434501,-3.622675],[40.434575,-3.622521],[40.435245,-3.622872],[40.435582,-3.623047],[40.436093,-3.623263],[40.436256,-3.623341],[40.436336,-3.623133],[40.436655,-3.622327],[40.437111,-3.621155],[40.437157,-3.621025],[40.437235,-3.620853],[40.43738,-3.620923],[40.437412,-3.620853],[40.438821,-3.621508]],line: null},
            {id: 8525,source: 7,target: 8,distance: null,duration: null,transportMode: "Bus",steps: [],"polyline": [[40.438802,-3.62158],[40.438907,-3.621629],[40.439046,-3.621693],[40.438993,-3.62185],[40.439137,-3.621922],[40.440057,-3.622386],[40.441066,-3.62301],[40.441124,-3.623046],[40.441342,-3.623154],[40.441597,-3.623202],[40.441608,-3.623203]],line: "105"},
            {id: 8526,source: 8,target: 9,distance: 0.75,duration: 9,transportMode: "A pie",steps: ["0-18:Camina hacia el sur","18-23:¡Gire a la izquierda!","23-29:¡Gire a la derecha!","29-35:¡Gire a la izquierda!","35-36:¡Gire a la izquierda a Calle de Alegría de Oria!","36-37:¡Gire a la derecha a Calle de Alcalá!","37-37:Llegar a Calle de Alcalá, a la derecha"],"polyline": [[40.441616,-3.623084],[40.4415,-3.623071],[40.441492,-3.623134],[40.441485,-3.623186],[40.441478,-3.623239],[40.441471,-3.623294],[40.441454,-3.623347],[40.441439,-3.623396],[40.441421,-3.62345],[40.441404,-3.623503],[40.441604,-3.623631],[40.441638,-3.623686],[40.441645,-3.623751],[40.441632,-3.623831],[40.441652,-3.62384],[40.441686,-3.623855],[40.441761,-3.623889],[40.441805,-3.62391],[40.441828,-3.62392],[40.441799,-3.624018],[40.441373,-3.625586],[40.441175,-3.626302],[40.441055,-3.626711],[40.441015,-3.626822],[40.441032,-3.626831],[40.440958,-3.627108],[40.440899,-3.627328],[40.440712,-3.628015],[40.44069,-3.628089],[40.440701,-3.628101],[40.440681,-3.628177],[40.440414,-3.629137],[40.4404,-3.629195],[40.440164,-3.630103],[40.440148,-3.630165],[40.440007,-3.630689],[40.439927,-3.630656],[40.439881,-3.630824]],line: null},
            {id: 8527,source: 9,target: 10,distance: null,duration: null,transportMode: "Bus",steps: [],"polyline": [[40.439881,-3.630824],[40.439797,-3.631133],[40.439731,-3.631382],[40.439626,-3.631779],[40.439586,-3.631929],[40.439516,-3.632185],[40.439486,-3.632292],[40.439471,-3.632348],[40.43932,-3.632916]],line: "77"},
            {id: 8528,source: 10,target: 11,distance: 0.74,duration: 9,transportMode: "A pie",steps: ["0-2:Camina hacia el oeste en Calle de Alcalá","2-3:¡Gire a la derecha a Calle de Riobamba!","3-11:¡Gire a la izquierda!","11-12:¡Gire a la izquierda a Calle del General Aranaz!","12-30:¡Gire a la derecha a Calle de Alcalá!","30-35:¡Gire a la derecha!","35-45:¡Gire ligeramente a la derecha a Calle de Alcalá!","45-45:Llegar a Calle de Alcalá, a la derecha"],"polyline": [[40.43932,-3.632916],[40.439319,-3.63292],[40.439226,-3.633237],[40.439292,-3.633266],[40.439279,-3.633316],[40.43908,-3.63402],[40.439069,-3.634076],[40.438978,-3.634407],[40.438813,-3.635003],[40.438693,-3.63544],[40.438727,-3.635467],[40.438697,-3.635559],[40.438608,-3.635495],[40.43859,-3.635563],[40.438466,-3.635998],[40.438412,-3.636078],[40.438239,-3.636726],[40.438165,-3.637006],[40.438094,-3.637274],[40.438051,-3.637435],[40.43797,-3.637737],[40.437924,-3.637912],[40.437905,-3.637974],[40.437888,-3.638033],[40.437866,-3.638097],[40.437834,-3.638186],[40.437774,-3.638354],[40.437728,-3.638482],[40.437685,-3.638602],[40.437664,-3.638655],[40.437648,-3.638693],[40.437657,-3.638739],[40.437664,-3.63885],[40.437618,-3.638947],[40.43759,-3.638967],[40.437513,-3.638991],[40.437489,-3.639042],[40.437453,-3.639117],[40.437292,-3.639463],[40.437192,-3.639665],[40.437098,-3.639857],[40.43706,-3.639935],[40.436977,-3.640107],[40.436914,-3.640237],[40.436869,-3.640331],[40.436746,-3.640585]],line: null},
            {id: 8529,source: 11,target: 14,distance: null,duration: null,transportMode: "Bus",steps: [],"polyline": [[40.436746,-3.640585],[40.43643,-3.641239],[40.436379,-3.641345],[40.436292,-3.641536],[40.436258,-3.641606],[40.436231,-3.64166],[40.436177,-3.641773],[40.435989,-3.64216],[40.435873,-3.642398],[40.435678,-3.642791],[40.435399,-3.643376],[40.435258,-3.643669],[40.435193,-3.643804],[40.435111,-3.643971],[40.435067,-3.644062],[40.435013,-3.644173],[40.434957,-3.644287],[40.434717,-3.64478],[40.434643,-3.644924],[40.434579,-3.645068],[40.434415,-3.645421],[40.434336,-3.645575],[40.434039,-3.64619],[40.434015,-3.646246],[40.433969,-3.646342],[40.433937,-3.646408],[40.433772,-3.646738],[40.433627,-3.647029],[40.433595,-3.647098],[40.433539,-3.64721],[40.433494,-3.647304],[40.43322,-3.647895],[40.433206,-3.647926],[40.433145,-3.648102],[40.433117,-3.648239],[40.433102,-3.648351],[40.433064,-3.648579],[40.433051,-3.648664],[40.433029,-3.648818],[40.432986,-3.649116],[40.432977,-3.649178],[40.432939,-3.649443],[40.432817,-3.650306],[40.432811,-3.650352],[40.432798,-3.650439],[40.432759,-3.650706],[40.432635,-3.651561],[40.432621,-3.651664],[40.432605,-3.651779],[40.432551,-3.652158],[40.432538,-3.652249],[40.432515,-3.652398],[40.432506,-3.652466],[40.432452,-3.652883]],line: "38"},
            {id: 8530,source: 14,target: 15,distance: 0.98,duration: 12,transportMode: "A pie",steps: ["0-5:Camina hacia el oeste en Calle de Alcalá","5-7:¡Gire a la derecha a Calle del Buen Gobernador!","7-14:¡Gire a la izquierda a Calle de Siena!","14-18:¡Gire a la derecha a Calle Ventas!","18-21:¡Gire a la izquierda a Calle Virgen de la Alegría!","21-25:¡Gire a la derecha!","25-37:¡Gire a la izquierda!","37-42:¡Gire bastante a la derecha a Avenida de los Toreros!","42-42:Llegar a Avenida de los Toreros, a la derecha"],"polyline": [[40.432452,-3.652883],[40.432426,-3.653082],[40.432397,-3.653277],[40.432381,-3.653392],[40.432277,-3.654148],[40.432264,-3.654247],[40.432374,-3.654274],[40.432876,-3.654393],[40.432736,-3.655393],[40.432732,-3.655426],[40.432719,-3.655519],[40.432702,-3.65563],[40.432582,-3.656406],[40.432542,-3.656691],[40.432493,-3.657047],[40.432978,-3.657166],[40.433029,-3.65726],[40.432961,-3.657773],[40.432941,-3.658031],[40.432823,-3.658189],[40.431947,-3.659164],[40.431766,-3.659387],[40.431777,-3.659464],[40.431687,-3.660078],[40.431703,-3.660153],[40.431724,-3.660149],[40.431732,-3.660365],[40.431735,-3.660422],[40.431743,-3.660547],[40.431738,-3.660706],[40.431718,-3.660842],[40.431685,-3.66099],[40.431687,-3.661052],[40.43169,-3.661131],[40.431617,-3.661299],[40.431543,-3.661435],[40.431482,-3.661497],[40.431456,-3.661524],[40.431498,-3.661564],[40.431647,-3.661653],[40.431848,-3.661673],[40.431885,-3.661666],[40.431977,-3.661656]],line: null},
            {id: 8531,source: 15,target: 16,distance: null,duration: null,transportMode: "Bus",steps: [],"polyline": [[40.431977,-3.661656],[40.432548,-3.661598],[40.43282,-3.66153],[40.432993,-3.661504],[40.433124,-3.66159],[40.433245,-3.661681],[40.43341,-3.661867],[40.433484,-3.662022],[40.433564,-3.6622],[40.433596,-3.662321],[40.433641,-3.662675],[40.433647,-3.662926],[40.433633,-3.663178],[40.433599,-3.663449],[40.43358,-3.663657],[40.433842,-3.663718],[40.433851,-3.66372]],line: "53"},
            {id: 8532,source: 16,target: 17,distance: 1.03,duration: 12,transportMode: "A pie",steps: ["0-3:Camina hacia el sur en Calle de Francisco Altimiras","3-14:¡Gire a la derecha a Avenida de los Toreros!","14-16:¡Gire a la derecha a Avenida de los Toreros!","16-21:¡Gire a la izquierda a Calle del Cardenal Belluga!","21-25:¡Gire ligeramente a la izquierda a Calle del Cardenal Belluga!","25-28:¡Gire a la derecha!","28-29:¡Gire a la derecha a Calle de Francisco Navacerrada!","29-30:¡Gire a la izquierda!","30-32:¡Gire a la derecha a Calle de Campanar!","32-39:¡Gire a la derecha a Calle de Florestán Aguilar!","39-40:¡Gire a la izquierda a Calle Cartagena!","40-41:¡Gire ligeramente a la izquierda a Calle de Francisco Silvela!","41-44:¡Gire a la derecha!","44-46:¡Gire bastante a la izquierda a Calle de Francisco Silvela!","46-47:¡Gire a la derecha a Calle de José Ortega y Gasset!","47-50:¡Gire a la derecha a Calle de José Ortega y Gasset!","50-50:Llegar a Calle de José Ortega y Gasset, a la derecha"],"polyline": [[40.433851,-3.66372],[40.433842,-3.663718],[40.43358,-3.663657],[40.43349,-3.663649],[40.433419,-3.664132],[40.433407,-3.664222],[40.433398,-3.664285],[40.433348,-3.664459],[40.433307,-3.664578],[40.433232,-3.664723],[40.433078,-3.664922],[40.432848,-3.665062],[40.432736,-3.66509],[40.432594,-3.665093],[40.43246,-3.665121],[40.432432,-3.665308],[40.432415,-3.665432],[40.432381,-3.665516],[40.432364,-3.665695],[40.432334,-3.665814],[40.432299,-3.665871],[40.432259,-3.665885],[40.431908,-3.665742],[40.431825,-3.665713],[40.431737,-3.665684],[40.431563,-3.665619],[40.431555,-3.665659],[40.43149,-3.66571],[40.431455,-3.665703],[40.431309,-3.66697],[40.431056,-3.667123],[40.430906,-3.667218],[40.430828,-3.667268],[40.430829,-3.667385],[40.430827,-3.667562],[40.430679,-3.668853],[40.430604,-3.669507],[40.430494,-3.67043],[40.430468,-3.670481],[40.430443,-3.670538],[40.430297,-3.670472],[40.430069,-3.670297],[40.429968,-3.670336],[40.429928,-3.670438],[40.429947,-3.67055],[40.429897,-3.670496],[40.429801,-3.670402],[40.429665,-3.670568],[40.429671,-3.670703],[40.429693,-3.671166],[40.429699,-3.671298]],line: null},
            {id: 8533,source: 17,target: 18,distance: 0.69,duration: 2,transportMode: "BiciMAD",steps: ["0-12:Camina hacia el oeste en Calle de José Ortega y Gasset","12-13:¡Gire a la izquierda a Calle del General Pardiñas!","13-13:Llegar a Calle del General Pardiñas, a la izquierda"],"polyline": [[40.429699,-3.671298],[40.429741,-3.672187],[40.429806,-3.673581],[40.429811,-3.673688],[40.429817,-3.673803],[40.429878,-3.675107],[40.429887,-3.675288],[40.429896,-3.675475],[40.429919,-3.675944],[40.429941,-3.676397],[40.429953,-3.676643],[40.429961,-3.676814],[40.43003,-3.678209],[40.429056,-3.678296]],line: null},
            {id: 8534,source: 18,target: 19,distance: 0.24,duration: 3,transportMode: "A pie",steps: ["0-2:Camina hacia el norte en Calle del General Pardiñas","2-3:¡Gire a la izquierda!","3-5:¡Gire a la izquierda!","5-6:¡Gire a la izquierda!","6-8:¡Gire bastante a la derecha a Calle de José Ortega y Gasset!","8-9:¡Gire bastante a la izquierda a Plaza del Marqués de Salamanca!","9-9:Llegar a Plaza del Marqués de Salamanca, a la derecha"],"polyline": [[40.429056,-3.678296],[40.43003,-3.678209],[40.430184,-3.678196],[40.43019,-3.678287],[40.430137,-3.678292],[40.430178,-3.679053],[40.430072,-3.679067],[40.430127,-3.679209],[40.430181,-3.679307],[40.430136,-3.6793]],line: null},
            {id: 8535,source: 19,target: 23,distance: null,duration: null,transportMode: "Metro",steps: [],"polyline": [[40.4301298212657,-3.67936371945562],[40.4331579372096,-3.68954159857549],[40.42852602883,-3.69643188600389],[40.4229331780798,-3.69761659244378],[40.4199866177357,-3.70180393013566]],line: "5"},
            {id: 8536,source: 23,target: 24,distance: null,duration: null,transportMode: "Metro",steps: [],"polyline": [[40.4199866177357,-3.70180393013566],[40.416876060894,-3.70326164857528]],line: "1"},
            {id: 8537,source: 24,target: 25,distance: 0.82,duration: 10,transportMode: "A pie",steps: ["0-1:Camina hacia el oeste en Plaza de la Puerta del Sol","1-11:¡Gire a la izquierda a Plaza de la Puerta del Sol!","11-13:¡Gire a la izquierda a Calle de las Hileras!","13-16:¡Gire a la derecha a Plaza de Herradores!","16-20:¡Gire bastante a la izquierda a Plaza de Herradores!","20-21:¡Gire a la derecha a Plaza del Comandante Las Morenas!","21-25:¡Siga todo recto a Costanilla de Santiago!","25-27:¡Gire a la derecha a Calle de Santiago!","27-31:¡Gire bastante a la izquierda a Plaza de Santiago!","31-33:¡Siga todo recto a Calle de la Cruzada!","33-33:Llegar a Calle de la Cruzada, a la izquierda"],"polyline": [[40.416854,-3.703253],[40.416832,-3.703345],[40.416806,-3.70372],[40.41679,-3.704319],[40.416791,-3.704562],[40.41683,-3.704705],[40.416975,-3.705429],[40.417091,-3.705914],[40.417116,-3.706021],[40.417263,-3.706636],[40.417388,-3.707159],[40.417571,-3.707768],[40.41752,-3.707812],[40.416771,-3.708467],[40.416749,-3.708539],[40.416741,-3.708632],[40.416753,-3.708672],[40.416702,-3.708636],[40.416462,-3.708595],[40.416399,-3.708613],[40.416377,-3.708638],[40.416318,-3.708821],[40.416309,-3.70886],[40.416249,-3.709133],[40.416178,-3.709553],[40.416133,-3.709592],[40.416193,-3.709815],[40.416539,-3.710645],[40.416481,-3.710651],[40.416542,-3.71079],[40.416494,-3.71102],[40.416474,-3.71108],[40.4164,-3.711266],[40.416402,-3.711316]],line: null}]
        /*
        id: 8514,
        name: "Ruta1",
        origin: {id: 8516,name: "40.44638865497956, -3.6090087890625004",longitude: -3.6090087890625004,latitude: 40.44638865497956},
        destination: {id: 8515,name: "40.409518722412905, -3.672866821289063",longitude: -3.672866821289063,latitude: 40.409518722412905},
        maxDistance: 1,
        transports: ["Metro","Bus","Cercanías","BiciMAD"],
        preferences: {"C_Instalaciones culturales": 4,"C_Parques y jardines": 5,"C_Escuelas de cocina y catas de vinos y aceites": 0,"C_Empresas de guías Turísticas": 0,"C_Edificios y monumentos": 3,"C_Centros de ocio": 0,"D_Deportes": 2,"T_Tiendas": 3,"R_Restauración": 2},
        date: new Date("2022-02-04"),
        points: [{id: 8516,name: "40.44638865497956, -3.6090087890625004",longitude: -3.6090087890625004,latitude: 40.44638865497956},{id: 5004,name: "Nectar-av.canillejas",longitude: -3.615277172363707,latitude: 40.44122432803461},{id: 5005,name: "Pza.cronos",longitude: -3.619036577532808,latitude: 40.43971780753817},{id: 3840,name: "Suanzes",longitude: -3.62683974417714,latitude: 40.4408512430777},{id: 3400,name: "Ciudad lineal",longitude: -3.6381575883455,latitude: 40.4380484084201},{id: 3297,name: "Pueblo nuevo",longitude: -3.64282293725217,latitude: 40.4356585154991},{id: 3842,name: "Quintana",longitude: -3.64736224209832,latitude: 40.4335849416495},{id: 3843,name: "El carmen",longitude: -3.65757254725966,latitude: 40.4318932920946},{id: 3360,name: "Ventas",longitude: -3.66360584172735,latitude: 40.4308848031949},{id: 3276,name: "Manuel becerra",longitude: -3.66920518071164,latitude: 40.4279044798375},{id: 3186,name: "O'donnell",longitude: -3.66859514629195,latitude: 40.4228884246446},{id: 3677,name: "Sainz de baranda",longitude: -3.66951374370264,latitude: 40.4150284968071},{id: 8515,name: "40.409518722412905, -3.672866821289063",longitude: -3.672866821289063,latitude: 40.409518722412905}],
        segments: [
          {id: 8517,source: 0,target: 1,distance: 0.9,duration: 11,transportMode: "A pie",steps: ["0-2:Camina hacia el sur en Calle de San Faustino","2-3:¡Siga todo recto a Calle de San Faustino!","3-8:¡Siga todo recto a Calle de San Faustino!","8-11:¡Gire a la derecha a Calle del Néctar!","11-23:¡Gire ligeramente a la izquierda a Calle del Néctar!","23-24:¡Gire a la derecha a Plaza Manuel Escobar!","24-26:¡Gire a la izquierda!","26-26:Llegar a su destino, a la izquierda"],polyline: [[40.446389,-3.608935],[40.446127,-3.608931],[40.445981,-3.608929],[40.445657,-3.608924],[40.444764,-3.608923],[40.444701,-3.60893],[40.444474,-3.608968],[40.4443,-3.60896],[40.444119,-3.608963],[40.444017,-3.609265],[40.443943,-3.609479],[40.443897,-3.609612],[40.443791,-3.60971],[40.443593,-3.609988],[40.443333,-3.610359],[40.442694,-3.611245],[40.442238,-3.611879],[40.442167,-3.612123],[40.442158,-3.612183],[40.442152,-3.612289],[40.442116,-3.612387],[40.441738,-3.613536],[40.441692,-3.613675],[40.441642,-3.613829],[40.441692,-3.61385],[40.441479,-3.614527],[40.441237,-3.615284]],line: null},
          {id: 8518,source: 1,target: 2,distance: null,duration: null,transportMode: "Bus",steps: [],polyline: [[40.441186,-3.615256],[40.441165,-3.615319],[40.44113,-3.615428],[40.441109,-3.615512],[40.441091,-3.615568],[40.441044,-3.615713],[40.440833,-3.616342],[40.440812,-3.616405],[40.440555,-3.617163],[40.440525,-3.617253],[40.440238,-3.618127],[40.440147,-3.618385],[40.440093,-3.618535],[40.440141,-3.618563],[40.440181,-3.618607],[40.44021,-3.618664],[40.440226,-3.618735],[40.440225,-3.618809],[40.440173,-3.618937],[40.440128,-3.61898],[40.440018,-3.619003],[40.439965,-3.618981],[40.439926,-3.618946],[40.439894,-3.618899],[40.439873,-3.618843],[40.439766,-3.618901],[40.439726,-3.618922],[40.439691,-3.618939]],line: "28"},
          {id: 8519,source: 2,target: 3,distance: 1.05,duration: 13,transportMode: "A pie",steps: ["0-2:Camina hacia el norte en Calle de Cronos","2-6:¡Gire a la izquierda!","6-7:¡Gire a la izquierda a Calle de Albasanz!","7-8:¡Gire a la derecha!","8-11:¡Gire a la izquierda!","11-13:¡Gire a la derecha!","13-17:¡Gire bastante a la derecha a Calle de San Romualdo!","17-19:¡Gire a la derecha a Calle de San Romualdo!","19-34:¡Gire a la izquierda!","34-43:¡Gire a la izquierda!","43-47:¡Gire a la izquierda!","47-48:¡Gire a la derecha!","48-50:¡Gire a la derecha!","50-51:¡Gire a la izquierda!","51-51:Llegar a su destino, a la izquierda"],polyline: [[40.439691,-3.618939],[40.439726,-3.618922],[40.439766,-3.618901],[40.439784,-3.618947],[40.439832,-3.619028],[40.439862,-3.619062],[40.439921,-3.619108],[40.439833,-3.619376],[40.439898,-3.619409],[40.439802,-3.619691],[40.439622,-3.620224],[40.439168,-3.621587],[40.439237,-3.621712],[40.439185,-3.621763],[40.440104,-3.622223],[40.441021,-3.622798],[40.441106,-3.62296],[40.441124,-3.623046],[40.441342,-3.623154],[40.441485,-3.623186],[40.441478,-3.623239],[40.441471,-3.623294],[40.441454,-3.623347],[40.441439,-3.623396],[40.441421,-3.62345],[40.441404,-3.623503],[40.441604,-3.623631],[40.441638,-3.623686],[40.441645,-3.623751],[40.441632,-3.623831],[40.441652,-3.62384],[40.441686,-3.623855],[40.441761,-3.623889],[40.441805,-3.62391],[40.441828,-3.62392],[40.441799,-3.624018],[40.441373,-3.625586],[40.441175,-3.626302],[40.441055,-3.626711],[40.441015,-3.626822],[40.44099,-3.626913],[40.440951,-3.627052],[40.440879,-3.627016],[40.440874,-3.627014],[40.440915,-3.626871],[40.440938,-3.626789],[40.440993,-3.626588],[40.441007,-3.62654],[40.440925,-3.626501],[40.440889,-3.626627],[40.440941,-3.626653],[40.440885,-3.626856]],line: null},
          {id: 8520,source: 3,target: 8,distance: null,duration: null,transportMode: "Metro",steps: [],polyline: [[40.4408512430777,-3.62683974417714],[40.4380484084201,-3.6381575883455],[40.4356585154991,-3.64282293725217],[40.4335849416495,-3.64736224209832],[40.4318932920946,-3.65757254725966],[40.4308848031949,-3.66360584172735]],line: "5"},
          {id: 8521,source: 8,target: 9,distance: null,duration: null,transportMode: "Metro",steps: [],polyline: [[40.4308848031949,-3.66360584172735],[40.4279044798375,-3.66920518071164]],line: "2"},
          {id: 8522,source: 9,target: 11,distance: null,duration: null,transportMode: "Metro",steps: [],polyline: [[40.4279044798375,-3.66920518071164],[40.4228884246446,-3.66859514629195],[40.4150284968071,-3.66951374370264]],line: "6-2"},
          {id: 8523,source: 11,target: 12,distance: 0.88,duration: 11,transportMode: "A pie",steps: ["0-1:Camina hacia el sur en Calle del Doctor Esquerdo","1-4:¡Gire a la derecha a Calle del Doctor Esquerdo!","4-8:¡Gire a la izquierda!","8-9:¡Gire a la derecha a Calle de Samaria!","9-15:¡Gire a la izquierda a Calle de Jesús Aprendiz!","15-16:¡Gire a la izquierda a Avenida de Nazaret!","16-19:¡Gire a la derecha a Calle de Jesús Aprendiz!","19-20:¡Gire a la derecha a Avenida de Nazaret!","20-24:¡Gire a la izquierda a Calle de Jesús Aprendiz!","24-25:¡Gire a la derecha a Calle de Arias Montano!","25-30:¡Gire a la izquierda a Calle de Antonio Díaz-Cañabate!","30-33:¡Gire a la derecha a Calle Juan de Jáuregui!","33-33:Llegar a Calle Juan de Jáuregui, a la derecha"],polyline: [[40.415033,-3.669605],[40.414409,-3.669664],[40.414415,-3.669773],[40.414419,-3.669861],[40.414456,-3.67059],[40.414394,-3.670593],[40.413543,-3.670657],[40.413269,-3.670677],[40.412924,-3.670699],[40.412928,-3.670807],[40.41236,-3.670839],[40.412251,-3.670903],[40.41212,-3.67099],[40.41201,-3.671029],[40.411568,-3.671053],[40.411478,-3.671057],[40.411478,-3.671031],[40.411448,-3.671012],[40.41141,-3.671017],[40.411386,-3.671042],[40.411387,-3.671065],[40.411321,-3.671071],[40.410905,-3.671106],[40.409965,-3.671187],[40.409864,-3.671195],[40.40991,-3.671604],[40.409866,-3.671618],[40.409713,-3.671664],[40.40956,-3.671718],[40.409401,-3.671844],[40.409235,-3.671976],[40.409325,-3.672418],[40.409426,-3.672866],[40.409442,-3.672912]],line: null}]
      */
      };
      //this.sharedService.showToast('No se ha podido obtener la ruta', 3000);
      //this.router.navigate(['/']);
    }

    // Iterate over the segments of the route
    this.route.segments.forEach((segment, index) => {
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
          let parts = step.split(':',2);
          // Get the source/target point & the instruction
          let wayPoints = parts[0].split('-', 2);
          let first = wayPoints[0];
          let last = wayPoints[1];
          let instruction = parts[1];
          steps.push({ first: first, last: last, instruction: instruction });
        });
        this.segmentsSteps.set(index, steps);
      }

      // Get the color for the segment
      let colorSegment = segment.line && segment.transportMode !== 'Bus' 
      ? colorSegments.get(segment.line) 
      : colorSegments.get(segment.transportMode);
      // Dash pattern for the segment (for walk segments)
      let dashedSegment = segment.transportMode === 'A pie';
      // Icon (mode of transport) for the segment
      let iconSegment = iconTransport.get(segment.transportMode);

      // Store some features of the segments
      this.segmentsVisual.push({ color: colorSegment, dashed: dashedSegment, icon: iconSegment });
    });
  }

  ionViewDidEnter() {
    this.isOpen = true;

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

    this.map = L.map('map', {
      zoomControl: false,
      center: [40.416694, -3.703250],
      zoom: 12,
      layers: [standard, satellite]
    });

    L.control.zoom({ position: 'topright' }).addTo(this.map);
    L.control.layers(layers, null, { position: 'topright' }).addTo(this.map);


    let multiPolyline: L.Polyline<LineString | MultiLineString, any>;
    let coordinates = [];
    // Iterate over the segments of the route
    this.route.segments.forEach((segment, index) => {
      // Transform number[][] to LatLng array in polyline
      let coords = segment.polyline.map(coord => new L.LatLng(coord[0], coord[1]));
      
      // Establish the color for the segment
      let color = this.segmentsVisual[index].color;
      // Dash pattern for the segment (for walk segments)
      let dashArray = this.segmentsVisual[index].dashed ? '1 7' : null;
      
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
      + this.segmentsVisual[index].icon + '">'
      + lineTooltip
      + '</div>'
      + this.route.points[segment.source].name 
      + '<ion-icon style="vertical-align: middle; font-size: 9px; opacity: 0.7" name="chevron-forward-outline"></ion-icon>' 
      + this.route.points[segment.target].name 
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
        iconUrl: './assets/' + this.segmentsVisual[index].icon,
        iconSize: [20, 20]
      });

      // Add markers to map
      new L.Marker([this.route.points[segment.source].latitude, this.route.points[segment.source].longitude], {
        icon: icon
      }).addTo(this.map).bindTooltip(this.route.points[segment.source].name);
    });

    // Marker for destination point
    new L.Marker([this.route.destination.latitude, this.route.destination.longitude], {
      icon: L.icon({ iconUrl: './assets/destination.png', iconSize: [25, 25]})
    }).addTo(this.map).bindTooltip(this.route.destination.name);

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

  // When step in walk or bike is selected
  zoomToStep(first: number, last: number, index: number) {
    // If there is already a polyline highlighted remove it from the map
    if (this.stepSelected) {
      this.map.removeLayer(this.stepSelected);
    }

    // Get coordinates to highlight & zoom
    let polyline = this.route.segments[index].polyline.slice(first, ++last)
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

}
