package fabnun.jsoupscheduler;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.FindIterable;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.text.Normalizer;
import org.json.JSONObject;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Node;
import org.jsoup.nodes.TextNode;
import org.jsoup.parser.Tag;
import org.jsoup.select.Elements;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.IndexOptions;
import com.mongodb.client.model.Indexes;
import com.mongodb.client.result.UpdateResult;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.json.JSONArray;
import org.jsoup.nodes.Element;

public final class Tools {

    public MongoDatabase DB;

    public static final String stopWords = " actualmente | acuerdo | adelante | ademas | ademas | adrede | afirmo | agrego | ahi | ahora | ahi | al | algo | alguna | algunas | alguno | algunos | algun | alli | alli | alrededor | ambos | ampleamos | antano | antano | ante | anterior | antes | apenas | aproximadamente | aquel | aquella | aquellas | aquello | aquellos | aqui | aquel | aquella | aquellas | aquellos | aqui | arriba | abajo | aseguro | asi | asi | atras | aun | aunque | ayer | anadio | aun | bajo | bastante | bien | breve | buen | buena | buenas | bueno | buenos | cada | casi | cerca | cierta | ciertas | cierto | ciertos | cinco | claro | comento | como | con | conmigo | conocer | conseguimos | conseguir | considera | considero | consigo | consigue | consiguen | consigues | contigo | contra | cosas | creo | cual | cuales | cualquier | cuando | cuanta | cuantas | cuanto | cuantos | cuatro | cuenta | cual | cuales | cuando | cuanta | cuantas | cuanto | cuantos | como | da | dado | dan | dar | debajo | debe | deben | debido | decir | dejo | delante | demasiado | demas | dentro | deprisa | desde | despacio | despues | despues | detras | detras | dia | dias | dice | dicen | dicho | dieron | diferente | diferentes | dijeron | dijo | dio | donde | dos | durante | dia | dias | donde | ejemplo | ella | ellas | ello | ellos | embargo | empleais | emplean | emplear | empleas | empleo | en | encima | encuentra | enfrente | enseguida | entonces | era | erais | eramos | eran | eras | eres | es | esa | esas | ese | eso | esos | esta | estaba | estabais | estaban | estabas | estad | estada | estadas | estado | estados | estais | estamos | estan | estando | estar | estaremos | estara | estaran | estaras | estare | estareis | estaria | estariais | estariamos | estarian | estarias | estas | este | estemos | esto | estos | estoy | estuve | estuviera | estuvierais | estuvieran | estuvieras | estuvieron | estuviese | estuvieseis | estuviesen | estuvieses | estuvimos | estuviste | estuvisteis | estuvieramos | estuviesemos | estuvo | esta | estabamos | estais | estan | estas | este | esteis | esten | estes | ex | excepto | existe | existen | explico | expreso | fin | final | fue | fuera | fuerais | fueran | fueras | fueron | fuese | fueseis | fuesen | fueses | fui | fuimos | fuiste | fuisteis | fueramos | fuesemos | gran | grandes | gueno | ha | haber | habia | habida | habidas | habido | habidos | habiendo | habla | hablan | habremos | habra | habran | habras | habre | habreis | habria | habriais | habriamos | habrian | habrias | habeis | habia | habiais | habiamos | habian | habias | hace | haceis | hacemos | hacen | hacer | hacerlo | haces | hacia | haciendo | hago | han | has | hasta | hay | haya | hayamos | hayan | hayas | hayais | he | hecho | hemos | hicieron | hizo | horas | hoy | hube | hubiera | hubierais | hubieran | hubieras | hubieron | hubiese | hubieseis | hubiesen | hubieses | hubimos | hubiste | hubisteis | hubieramos | hubiesemos | hubo | igual | incluso | indico | informo | informo | intenta | intentais | intentamos | intentan | intentar | intentas | intento | ir | junto | lado | largo | le | lejos | les | llego | lleva | llevar | luego | lugar | mal | manera | manifesto | mas | mayor | me | mediante | medio | mejor | menciono | menos | menudo | mi | mia | mias | mientras | mio | mios | mis | misma | mismas | mismo | mismos | modo | momento | mucha | muchas | mucho | muchos | muy | mas | mi | mia | mias | mio | mios | nada | nadie | ni | ninguna | ningunas | ninguno | ningunos | ningun | no | nos | nosotras | nosotros | nuestra | nuestras | nuestro | nuestros | nueva | nuevas | nuevo | nuevos | nunca | ocho | os | otra | otras | otro | otros | pais | para | parece | parte | partir | pasada | pasado | pais | peor | pero | pesar | poca | pocas | poco | pocos | podeis | podemos | poder | podria | podriais | podriamos | podrian | podrias | podra | podran | podria | podrian | poner | por | por +que | porque | posible | primer | primera | primero | primeros | principalmente | pronto | propia | propias | propio | propios | proximo | proximo | proximos | pudo | pueda | puede | pueden | puedo | pues | qeu | que | quedo | queremos | quien | quienes | quiere | quiza | quizas | quiza | quizas | quien | quienes | que | raras | realizado | realizar | realizo | repente | respecto | sabe | sabeis | sabemos | saben | saber | sabes | sal | salvo | se | sea | seamos | sean | seas | segun | segunda | segundo | segun | seis | ser | sera | seremos | sera | seran | seras | sere | sereis | seria | seriais | seriamos | serian | serias | seais | senalo | si | sido | siempre | siendo | siete | sigue | siguiente | sin | sino | sobre | sois | sola | solamente | solas | solo | solos | somos | son | soy | soyos | su | supuesto | sus | suya | suyas | suyo | suyos | se | si | solo | tal | tambien | tambien | tampoco | tan | tanto | tarde | te | temprano | tendremos | tendra | tendran | tendras | tendre | tendreis | tendria | tendriais | tendriamos | tendrian | tendrias | tened | teneis | tenemos | tener | tenga | tengamos | tengan | tengas | tengo | tengais | tenida | tenidas | tenido | tenidos | teniendo | teneis | tenia | teniais | teniamos | tenian | tenias | tercera | ti | tiempo | tiene | tienen | tienes | toda | todas | todavia | todavia | todo | todos | total | trabaja | trabajais | trabajamos | trabajan | trabajar | trabajas | trabajo | tras | trata | traves | tres | tu | tus | tuve | tuviera | tuvierais | tuvieran | tuvieras | tuvieron | tuviese | tuvieseis | tuviesen | tuvieses | tuvimos | tuviste | tuvisteis | tuvieramos | tuviesemos | tuvo | tuya | tuyas | tuyo | tuyos | tu | ultimo | un | una | unas | uno | unos | usa | usais | usamos | usan | usar | usas | uso | usted | ustedes | va | vais | valor | vamos | van | varias | varios | vaya | veces | ver | verdad | verdadera | verdadero | vez | vosotras | vosotros | voy | vuestra | vuestras | vuestro | vuestros | ya | yo | eramos | esa | esas | ese | esos | esta | estas | este | estos | ultimas | ultimo | ultimos | null | void ";

    public static final String localWords = "Abránquil|Achao|Agua Buena|Algarrobal Punta El Olivo|Algarrobito|Algarrobo|Alhué|Alto Bío Bío|Alto del Carmen|Alto El Manzano|Alto Hospicio|Altovalsol|Ancud|Andacollo|Angol|Angostura|Antártica|Antártica Chilena|Antiguala|Antofagasta|Antuco|Arauco|Arica|Artificio|Auquinco|Aysén|Aysén del General Carlos Ibáñez|Aysén del General Carlos Ibáñez del Campo|Bahía Mansa Maicolpue|Bío Bío|biobio|Bobadilla|Bollenar|Buin|Bulnes|Cabildo|Cabo de Hornos|Cabrero|Cachagua|Cachapoal|Calama|Calbuco|Caldera|Calera de Tango|Caleta San Pedro|Caleta Tumbes|Calle Larga|Camarones|Camiña|Campanario|Canela|Canela Baja|Cañete|Capitán Pastene|Capitán Prat|Carahue|Carampangue|Cardenal Caro|Carelmapu|Cartagena|Casablanca|Castro|Catapilco|Catemu|Cauquenes|Cautín|Cerrillos|Cerro|Moreno  Cerro Navia|Cerro Pataguas|Chacabuco|Chacarillas|Chada|Chaitén|Chañaral|Chañaral Alto|Chanco|Chépica|Cherquenco|Chiguayante|Chile Chico|Chillán|Chillán Viejo|Chillepín|Chiloé|Chimbarongo|Chincolco|Choapa|Chol Chol|Cholchol|Chonchi|Chorombo Bajo|Cinco Caminos|Cisnes|Cobquecura|Cochamó|Cochrane|Codegua|Coelemu|Coihue|Coihueco|Coihuin|Coinco|Colbún|Colchagua|Colchane|Colina|Collipulli|Coltauco|Combarbalá|Coñaripe|Concepción|Conchalí|Concón|Constitución|Contulmo|Copiapó|Copihue|Coquimbo|Cordillera|Coronel|Corral|Corte Alto|Coya|Coyhaique|Cumpeo|Cunaco|Cunco|Curacautín|Curacaví|Curaco de Vélez|Curanilahue|Curanipe|Curarrehue|Curepto|Curicó|Curimón|Dalcahue|de Antofagasta|de Arica y Parinacota|de Atacama|de Coquimbo|de la Antártica Chilena|de la Araucanía|de Los Lagos|de Los Ríos|de Magallanes |de Magallanes y de la Antártica Chilena|de Ñuble|de Tarapacá|de Valparaíso|del Biobío|del Libertador General|del Libertador General Bernardo O’Higgins|del Maule|Antofagasta|Arica y Parinacota|Atacama|Coquimbo|la Antártica Chilena|la Araucanía|Los Lagos|Los Ríos|Magallanes |Magallanes y de la Antártica Chilena|Ñuble|Tarapacá|Valparaíso|Biobío|Libertador General|Libertador General Bernardo O’Higgins|Maule|Dichato|Diego de Almagro|Diguillín|Doñihue|El Bosque|El Cambucho|El Carmen|El Carmen La Higuera|El Colorado|El Emboque|El Higueral|El Llano|El Loa|El Maitén|El Manzano (Las Cabras)|El Manzano (San Vicente)|El Monte|El Peñón|El Principal|El Quisco|El Roto Chileno|El Rulo|El Tabo|El Tambo|El Tepual|El Tránsito|Elqui|Empedrado|Entre Lagos|Ercilla|Esmeralda|Estación Central|Estación Colina|Estación Polpaico|Estación Villa Alegre|Estación Yumbel|Florida|Freire|Freirina|Fresia|Frutillar|Futaleufú|Futrono|Galvarino|General Carrera|General Lagos|General López|Gorbea|Graneros|Guaitecas|Guamalata|Guanaqueros|Hijuelas|Hualaihué|Hualañé|Hualpén|Hualpín|Hualqui|Huara|Huasco|Huasco Bajo|Huechuraba|Huelquén|Huertos Familiares|Huilquilemu|Huiscapi|Ibacache Chorombo|Illapel|Iloca|Independencia|Iquique|Isla de Maipo|Isla de Pascua|Itata|Juan Fernández|La Caldera|La Calera|La Chimba|La Cisterna|La Compañía|La Cruz|La Cuesta|La Esperanza El Cortijo|La Estrella|La Florida|La Granja|La Higuera|La Isla|La Junta|La Laguna de Zapallar|La Ligua|La Pintana|La Reina|La Serena|La Unión|La Vara|Lago Ranco|Lago Verde|Laguna Blanca|Laguna Verde|Laja|Lampa|Lanco|Las Cabras|Las Cabritas|Las Canteras|Las Condes|Las Mariposas|Las Obras|Lastarria|Lautaro|Lebu|Lican Ray|Licanray|Licantén|Limache|Limarí|Linares|Litueche|Llanquihue|Llay Llay|Llico|Lo Aguirre|Lo Arcay Casas Viejas|Lo Barnechea|Lo Castro|Lo Espejo|Lo Figueroa|Lo Herrera|Lo Prado|Lolol|Loncoche|Longaví|Lonquén|Lonquimay|Loreto Molino|Los Álamos|Los Andes|Los Ángeles|Los Cristales|Los Ingleses|Los Lagos|Los Laureles|Los Muermos|Los Pellines|Los Rulos|Los Sauces|Los Vilos|Lota|Lumaco|Machalí|Macul|Máfil|Magallanes|Maipo|Maipú|Maitencillo|Malalhue|Malleco|Malloa|Mañihuales|Marchigüe|Marchihue|Marga Marga|María Elena|María Pinto|Mariquina|Maule|Maullín|Mejillones|Melinka|Melipeuco|Melipilla|Metropolitana|Metropolitana de Santiago|Millantú|Mininco|Mirador de Puerto Varas|Molina|Monte Patria|Mostazal|Mulchén|Nacimiento|Nancagua|Ñancul|Navidad|Negrete|Neltume|Niebla|Ninhue|Ñipas|Ñiquén|Nogales|Nontuela|Noviciado Alto|Nueva Braunau|Nueva Imperial|Nueva Talcuna|Nueva Toltén|Ñuñoa|O’Higgins|Olivar|Olivar Alto|Olivar Bajo|Ollagüe|Olmué|Orilla de Maule|Osorno|Ovalle|Pabellón|Padre Hurtado|Padre Las Casas|Paihuano|Pailahueque|Paillaco|Paine|Palena|Palmilla|Panguilemo|Panguipulli|Panimávida|Panquehue|Papudo|Paredones|Parinacota|Parral|Pataguas Orilla|Pedro Aguirre Cerda|Pehuén|Pelarco|Pelequén|Pelluhue|Pemuco|Peñaflor|Peñalolén|Pencahue|Penco|Peñuelas|Peralillo|Perquenco|Petorca|Peumo|Pica|Pichidangui|Pichidegua|Pichilemu|Pillanlelbún|Pinto|Pintué La Guachera|Pirque|Pitrufquén|Placilla|Polcura|Pomaire|Portal San Francisco|Portezuelo|Porvenir|Pozo Almonte|Primavera|Providencia|Puchuncaví|Pucón|Pudahuel|Pueblo Nuevo|Pueblo Seco|Puente Alto|Puente Negro|Puente Ñuble|Puerto Chacabuco|Puerto Cisnes|Puerto Montt|Puerto Natales|Puerto Octay|Puerto Saavedra|Puerto Varas|Pullally|Pumanque|Punilla|Punitaqui|Punta Arenas|Punta de Cortés|Punta de Parra|Puqueldón|Purén|Purranque|Putaendo|Putre|Putú|Puyehue|Queilén|Quellón|Quemchi|Quepe|Queri|Queule|Quidico|Quilaco|Quilapán Polonia|Quilicura|Quilimarí Alto|Quilleco|Quillón|Quillota|Quilpué|Quinchamalí|Quinchao|Quinta de Tilcoco|Quinta Normal|Quintero|Quirihue|Quiriquina|Rabuco|Rafael|Ramadillas|Rancagua|Ranco|Ranguelmo|Ránquil|Rastrojos|Rauco|Recinto Los Lleuques|Recoleta|Metropolitana de Santiago|Renaico|Renca|Rengo|Requegua|Requínoa|Retiro|Reumén|Rinconada|Rinconada de Alcones|Rinconada de Parral|Río Bueno|Río Claro|Río Hurtado|Río Ibáñez|Río Negro|Río Verde|Romeral|Rosario|Rosario Codao Carretera|Saavedra|Sagrada Familia|Salamanca|San Alberto|San Alfonso|San Antonio|San Antonio de Naltagua|San Bernardo|San Carlos|San Carlos de Purén|San Clemente|San Esteban|San Fabián|San Fabián de Alico|San Felipe|San Felipe de Aconcagua|San Fernando|San Gregorio|San Gregorio de Ñiquén|San Ignacio|San Javier|San Joaquín|San José|San José de Maipo|San José del Carmen|San Juan de la Costa|San Miguel|San Nicolás|San Pablo|San Pedro|San Pedro de Atacama|San Pedro de la Paz|San Rafael|San Ramón|San Rosendo|San Vicente de Tagua Tagua|Santa Amalia|Santa Bárbara|Santa Clara|Santa Cruz|Santa Elena|Santa Fe|Santa Juana|Santa María|Santa Marta de Liray|Santa Rosa|Santiago|Santo Domingo|Senda Sur|Sierra Gorda|Sol de Septiembre|Sotaquí|Talagante|Talca|Talcahuano|Talcamávida|Taltal|Tamarugal|Temuco|Teno|Teodoro Schmidt|Tierra Amarilla|Tierra del Fuego|Tijeral|Til Til|Timaukel|Tinguiririca|Tirúa|Tocopilla|Toltén|Tomé|Torres del Paine|Tortel|Traiguén|Treguaco|Trehuaco|Trovolhue|Tucapel|Última Esperanza|Valdivia|Valdivia de Paine|Valle Hermoso|Vallenar|Valparaíso|Vara Gruesa|Vichuquén|Victoria|Vicuña|Vicuña Mackenna|Vilcún|Villa Alegre|Villa Alemana|Villa Alhué|Villa Campo Alegre|Villa Illinois|Villa Los Niches|Villa Mercedes|Villa Prat|Villa San Ramón II|Villa Santa Luisa|Villarrica|Viluco|Viña del Mar|Virquenco|Vitacura|Yerbas Buenas|Yumbel|Yungay|Zapallar";

    public static final String localWordsNormal = " abranquil | achao | agua +buena | algarrobal +punta +el +olivo | algarrobito | algarrobo | alhue | alto +bio +bio | alto +del +carmen | alto +el +manzano | alto +hospicio | altovalsol | ancud | andacollo | angol | angostura | antartica | antartica +chilena | antiguala | antofagasta | antuco | arauco | arica | artificio | auquinco | aysen | aysen +del +general +carlos +ibanez | aysen +del +general +carlos +ibanez +del +campo | bahia +mansa +maicolpue | bio +bio | biobio | bobadilla | bollenar | buin | bulnes | cabildo | cabo +de +hornos | cabrero | cachagua | cachapoal | calama | calbuco | caldera | calera +de +tango | caleta +san +pedro | caleta +tumbes | calle +larga | camarones | camina | campanario | canela | canela +baja | canete | capitan +pastene | capitan +prat | carahue | carampangue | cardenal +caro | carelmapu | cartagena | casablanca | castro | catapilco | catemu | cauquenes | cautin | cerrillos | cerro | moreno +cerro +navia | cerro +pataguas | chacabuco | chacarillas | chada | chaiten | chanaral | chanaral +alto | chanco | chepica | cherquenco | chiguayante | chile +chico | chillan | chillan +viejo | chillepin | chiloe | chimbarongo | chincolco | choapa | chol +chol | cholchol | chonchi | chorombo +bajo | cinco +caminos | cisnes | cobquecura | cochamo | cochrane | codegua | coelemu | coihue | coihueco | coihuin | coinco | colbun | colchagua | colchane | colina | collipulli | coltauco | combarbala | conaripe | concepcion | conchali | concon | constitucion | contulmo | copiapo | copihue | coquimbo | cordillera | coronel | corral | corte +alto | coya | coyhaique | cumpeo | cunaco | cunco | curacautin | curacavi | curaco +de +velez | curanilahue | curanipe | curarrehue | curepto | curico | curimon | dalcahue | de +antofagasta | de +arica +y +parinacota | de +atacama | de +coquimbo | de +la +antartica +chilena | de +la +araucania | de +los +lagos | de +los +rios | de +magallanes | de +magallanes +y +de +la +antartica +chilena | de +nuble | de +tarapaca | de +valparaiso | del +biobio | del +libertador +general | del +libertador +general +bernardo +o +higgins | del +maule | antofagasta | arica +y +parinacota | atacama | coquimbo | la +antartica +chilena | la +araucania | los +lagos | los +rios | magallanes | magallanes +y +de +la +antartica +chilena | nuble | tarapaca | valparaiso | biobio | libertador +general | libertador +general +bernardo +o +higgins | del +maule | dichato | diego +de +almagro | diguillin | donihue | el +bosque | el +cambucho | el +carmen | el +carmen +la +higuera | el +colorado | el +emboque | el +higueral | el +llano | el +loa | el +maiten | el +manzano +las +cabras | el +manzano +san +vicente | el +monte | el +penon | el +principal | el +quisco | el +roto +chileno | el +rulo | el +tabo | el +tambo | el +tepual | el +transito | elqui | empedrado | entre +lagos | ercilla | esmeralda | estacion +central | estacion +colina | estacion +polpaico | estacion +villa +alegre | estacion +yumbel | florida | freire | freirina | fresia | frutillar | futaleufu | futrono | galvarino | general +carrera | general +lagos | general +lopez | gorbea | graneros | guaitecas | guamalata | guanaqueros | hijuelas | hualaihue | hualane | hualpen | hualpin | hualqui | huara | huasco | huasco +bajo | huechuraba | huelquen | huertos +familiares | huilquilemu | huiscapi | ibacache +chorombo | illapel | iloca | independencia | iquique | isla +de +maipo | isla +de +pascua | itata | juan +fernandez | la +caldera | la +calera | la +chimba | la +cisterna | la +compania | la +cruz | la +cuesta | la +esperanza +el +cortijo | la +estrella | la +florida | la +granja | la +higuera | la +isla | la +junta | la +laguna +de +zapallar | la +ligua | la +pintana | la +reina | la +serena | la +union | la +vara | lago +ranco | lago +verde | laguna +blanca | laguna +verde | laja | lampa | lanco | las +cabras | las +cabritas | las +canteras | las +condes | las +mariposas | las +obras | lastarria | lautaro | lebu | lican +ray | licanray | licanten | limache | limari | linares | litueche | llanquihue | llay +llay | llico | lo +aguirre | lo +arcay +casas +viejas | lo +barnechea | lo +castro | lo +espejo | lo +figueroa | lo +herrera | lo +prado | lolol | loncoche | longavi | lonquen | lonquimay | loreto +molino | los +alamos | los +andes | los +angeles | los +cristales | los +ingleses | los +lagos | los +laureles | los +muermos | los +pellines | los +rulos | los +sauces | los +vilos | lota | lumaco | machali | macul | mafil | magallanes | maipo | maipu | maitencillo | malalhue | malleco | malloa | manihuales | marchigue | marchihue | marga +marga | maria +elena | maria +pinto | mariquina | maule | maullin | mejillones | melinka | melipeuco | melipilla | metropolitana | metropolitana +de +santiago | millantu | mininco | mirador +de +puerto +varas | molina | monte +patria | mostazal | mulchen | nacimiento | nancagua | nancul | navidad | negrete | neltume | niebla | ninhue | nipas | niquen | nogales | nontuela | noviciado +alto | nueva +braunau | nueva +imperial | nueva +talcuna | nueva +tolten | nunoa | o +higgins | olivar | olivar +alto | olivar +bajo | ollague | olmue | orilla +de +maule | osorno | ovalle | pabellon | padre +hurtado | padre +las +casas | paihuano | pailahueque | paillaco | paine | palena | palmilla | panguilemo | panguipulli | panimavida | panquehue | papudo | paredones | parinacota | parral | pataguas +orilla | pedro +aguirre +cerda | pehuen | pelarco | pelequen | pelluhue | pemuco | penaflor | penalolen | pencahue | penco | penuelas | peralillo | perquenco | petorca | peumo | pica | pichidangui | pichidegua | pichilemu | pillanlelbun | pinto | pintue +la +guachera | pirque | pitrufquen | placilla | polcura | pomaire | portal +san +francisco | portezuelo | porvenir | pozo +almonte | primavera | providencia | puchuncavi | pucon | pudahuel | pueblo +nuevo | pueblo +seco | puente +alto | puente +negro | puente +nuble | puerto +chacabuco | puerto +cisnes | puerto +montt | puerto +natales | puerto +octay | puerto +saavedra | puerto +varas | pullally | pumanque | punilla | punitaqui | punta +arenas | punta +de +cortes | punta +de +parra | puqueldon | puren | purranque | putaendo | putre | putu | puyehue | queilen | quellon | quemchi | quepe | queri | queule | quidico | quilaco | quilapan +polonia | quilicura | quilimari +alto | quilleco | quillon | quillota | quilpue | quinchamali | quinchao | quinta +de +tilcoco | quinta +normal | quintero | quirihue | quiriquina | rabuco | rafael | ramadillas | rancagua | ranco | ranguelmo | ranquil | rastrojos | rauco | recinto +los +lleuques | recoleta | metropolitana +de +santiago | renaico | renca | rengo | requegua | requinoa | retiro | reumen | rinconada | rinconada +de +alcones | rinconada +de +parral | rio +bueno | rio +claro | rio +hurtado | rio +ibanez | rio +negro | rio +verde | romeral | rosario | rosario +codao +carretera | saavedra | sagrada +familia | salamanca | san +alberto | san +alfonso | san +antonio | san +antonio +de +naltagua | san +bernardo | san +carlos | san +carlos +de +puren | san +clemente | san +esteban | san +fabian | san +fabian +de +alico | san +felipe | san +felipe +de +aconcagua | san +fernando | san +gregorio | san +gregorio +de +niquen | san +ignacio | san +javier | san +joaquin | san +jose | san +jose +de +maipo | san +jose +del +carmen | san +juan +de +la +costa | san +miguel | san +nicolas | san +pablo | san +pedro | san +pedro +de +atacama | san +pedro +de +la +paz | san +rafael | san +ramon | san +rosendo | san +vicente +de +tagua +tagua | santa +amalia | santa +barbara | santa +clara | santa +cruz | santa +elena | santa +fe | santa +juana | santa +maria | santa +marta +de +liray | santa +rosa | santiago | santo +domingo | senda +sur | sierra +gorda | sol +de +septiembre | sotaqui | talagante | talca | talcahuano | talcamavida | taltal | tamarugal | temuco | teno | teodoro +schmidt | tierra +amarilla | tierra +del +fuego | tijeral | til +til | timaukel | tinguiririca | tirua | tocopilla | tolten | tome | torres +del +paine | tortel | traiguen | treguaco | trehuaco | trovolhue | tucapel | ultima +esperanza | valdivia | valdivia +de +paine | valle +hermoso | vallenar | valparaiso | vara +gruesa | vichuquen | victoria | vicuna | vicuna +mackenna | vilcun | villa +alegre | villa +alemana | villa +alhue | villa +campo +alegre | villa +illinois | villa +los +niches | villa +mercedes | villa +prat | villa +san +ramon +ii | villa +santa +luisa | villarrica | viluco | vina +del +mar | virquenco | vitacura | yerbas +buenas | yumbel | yungay | zapallar ";

    public static final HashMap<String, String> localMap = new HashMap<>();

    static {
        String[] s1 = localWords.split("\\|");
        String[] s2 = localWordsNormal.split("\\|");
        for (int i = 0; i < s2.length; i++) {
            localMap.put(s2[i].replaceAll("\\+", " ").replaceAll("\\s+", " "), s1[i]);
        }
    }

    String agent;
    int timeOut;

    public Tools(String uri, String database, String indexes, String agent, int timeOut) {
        this.agent = agent;
        this.timeOut = timeOut;
        DB = new MongoClient(new MongoClientURI(uri)).getDatabase(database);
        String dbIndexes = indexes;
        if (dbIndexes != null && !dbIndexes.trim().isEmpty()) {
            for (String s : dbIndexes.split(",")) {
                s = s.trim();
                try {
                    int idx = s.indexOf(".");
                    int idx1 = s.lastIndexOf(".");
                    String coll = s.substring(0, idx);
                    String type = s.substring(idx1 + 1);
                    boolean unique = type.endsWith("!");
                    if (unique) {
                        type = type.substring(0, type.length() - 1);
                    }
                    s = s.substring(idx + 1, idx1);
                    String[] ss = s.split("\\.");
                    switch (type) {
                        case "asc":
                            dbIdxAscending(coll, ss, unique);
                            break;
                        case "des":
                            dbIdxDescending(coll, ss, unique);
                            break;
                        case "txt":
                            for (String sss : ss) {
                                dbIdxText(coll, sss, unique);
                            }
                            break;
                        case "geo":
                            dbIdxGeo(coll, ss, unique);
                            break;
                        default:
                            err("Error en indice: " + s);
                            break;
                    }
                } catch (Exception e) {
                    err("ERROR MONGODB INDEX " + s);
                    err(e.getLocalizedMessage());
                }
            }
        }
    }

    //-----------------------------------------------------
    //				      funciones para textos
    public String textEncodeURI(String uri) throws MalformedURLException, URISyntaxException {
        URL url = new URL(uri);
        URI uri2 = new URI(url.getProtocol(), url.getUserInfo(), url.getHost(), url.getPort(), url.getPath(), url.getQuery(), url.getRef());
        return uri2.toASCIIString();
    }

    public String textUnescape(String htmlText) {
        return org.jsoup.parser.Parser.unescapeEntities(htmlText, true);
    }

    public String textNormalize(String text) {
        return textNormalize(text, true);
    }

    public double textCompare(String s1, String s2) {
        info.debatty.java.stringsimilarity.RatcliffObershelp lcs
                = new info.debatty.java.stringsimilarity.RatcliffObershelp();
        s1 = s1 == null ? "" : s1;
        s2 = s2 == null ? "" : s2;
        return lcs.distance(s1, s2);
    }

    public String textNormalize(String text, boolean removeStopWords) {

        if (text == null) {
            return null;
        }
        text = Jsoup.parse(text).text();
        text = text.replaceAll("ñ|Ñ", "n").
                replaceAll("á|Á", "a").
                replaceAll("é|É", "e").
                replaceAll("í|Í", "i").
                replaceAll("ó|Ó", "o").
                replaceAll("ú|Ú", "u").
                replaceAll("à|À", "a").
                replaceAll("è|È", "e").
                replaceAll("ì|Ì", "i").
                replaceAll("ò|Ò", "o").
                replaceAll("ù|Ù", "u").
                replaceAll("ä|Ä", "a").
                replaceAll("ë|Ë", "e").
                replaceAll("ï|Ï", "i").
                replaceAll("ö|Ö", "o").
                replaceAll("ü|Ü", "u").
                replaceAll("\\W", " ");

        text = Normalizer.normalize(text, Normalizer.Form.NFD);
        text = " " + text.replaceAll("[\\p{InCombiningDiacriticalMarks}]+", " ").replaceAll("\\s+", " ") + " ";
        if (removeStopWords) {

            text = (" " + text + " ").replaceAll("\\s+", "  ").replaceAll(stopWords, " ");
        }
        return text.replaceAll("\\s+", " ").trim().toLowerCase();
    }

    public String textLocalMatch(String text) {
        Pattern p = Pattern.compile(localWordsNormal);
        text = (" " + text + " ").replaceAll("\\s+", "  ");
        Matcher m = p.matcher(text);
        HashSet<String> map = new HashSet<>();
        while (m.find()) {
            String g = m.group();
            //String v =localMap.get(g.replaceAll("\\s+", " ")).trim();
            HashSet<String> remove = new HashSet<>();
            for (String k : map) {
                if (g.contains(k)) {
                    remove.add(k);
                } else if (k.contains(g)) {
                    remove = null;
                    break;
                }
            }
            if (remove != null) {
                for (String r : remove) {
                    map.remove(r);
                }
                map.add(g);
            }
        }
        HashSet<String> resp = new HashSet<>();
        for (String k : map) {
            resp.add(localMap.get(k.replaceAll("\\s+", " ")).trim());
        }
        String r = resp.toString();
        return r.substring(1, r.length() - 1);
    }

    public String textMatch(String text, String regExp) {
        regExp = (" " + regExp.replaceAll(" ", " +").replaceAll("\\|", " | ") + " ").replaceAll("\\s+", "  ");
        Pattern p = Pattern.compile(regExp);
        Matcher m = p.matcher((" " + text + " ").replaceAll("\\s+", "  "));
        HashSet<String> map = new HashSet<>();
        while (m.find()) {
            map.add(m.group().replaceAll("\\s+", " ").trim());
        }
        String r = map.toString();
        return r.substring(1, r.length() - 1);
    }

    //-----------------------------------------------------
    //			              funciones del crawler
    public JSONObject domLoadJSON(String uri) throws IOException, MalformedURLException, URISyntaxException {
        String string = Jsoup.connect(textEncodeURI(uri)).timeout(timeOut).userAgent(agent).ignoreContentType(true).execute().body();
        return new JSONObject(string);
    }

    public JSONObject domLoadJSON(String uri, String select, boolean post, String... params) throws IOException, MalformedURLException, URISyntaxException {
        Connection conn = Jsoup.connect(uri).userAgent(agent);
        if (params != null) {
            for (int i = 0; i < params.length; i += 2) {
                conn = conn.data(params[i], params[(i + 1)]);
            }
        }
        conn = conn.timeout(timeOut);
        if (post) {
            conn.post();
        } else {
            conn.get();
        }
        String string = conn.ignoreContentType(true).execute().body();
        return new JSONObject(string);
    }

    public JSONArray domLoadJSONArray(String uri) throws IOException, MalformedURLException, URISyntaxException {
        String string = Jsoup.connect(textEncodeURI(uri)).timeout(timeOut).userAgent(agent).ignoreContentType(true).execute().body();
        return new JSONArray(string);
    }

    public JSONArray domLoadJSONArray(String uri, String select, boolean post, String... params) throws IOException, MalformedURLException, URISyntaxException {
        Connection conn = Jsoup.connect(uri).userAgent(agent);
        if (params != null) {
            for (int i = 0; i < params.length; i += 2) {
                conn = conn.data(params[i], params[(i + 1)]);
            }
        }
        conn = conn.timeout(timeOut);
        if (post) {
            conn.post();
        } else {
            conn.get();
        }
        String string = conn.ignoreContentType(true).execute().body();
        return new JSONArray(string);
    }

    public Node domLoad(String uri) throws IOException {
        return domLoad(uri, null, false, (String[]) null);
    }

    public Node domLoad(String uri, String select) throws IOException, MalformedURLException, URISyntaxException {
        return domLoad(textEncodeURI(uri), select, false, (String[]) null);
    }

    public Node domLoad(String uri, String select, boolean post, String... params) throws IOException {
        int parentLevel = 0;
        if (select != null && select.matches(".*!\\d+")) {
            int idx = select.lastIndexOf("!");
            parentLevel = Integer.parseInt(select.substring(idx + 1));
            select = select.substring(0, idx);
        }
        Connection conn = Jsoup.connect(uri).userAgent(agent);
        if (params != null) {
            for (int i = 0; i < params.length; i += 2) {
                conn = conn.data(params[i], params[(i + 1)]);
            }
        }
        conn = conn.timeout(timeOut);
        org.jsoup.nodes.Document doc;
        if (post) {
            doc = conn.post().normalise();
        } else {
            doc = conn.get().normalise();
        }

        //https://www.computrabajo.cl/ofertas-de-trabajo/?q=trabajo social&by=publicationtime
        //div.iO h2 a
        Node node;
        if ((select != null) && (!select.isEmpty())) {
            Elements elements = doc.select(select);
            Element html = new Element(Tag.valueOf("html"), uri);
            for (Element e : elements) {
                for (int i = 0; i < parentLevel; i++) {
                    e = e.parent();
                }
                html.appendChild(e);
            }
            node = html;
        } else {
            node = doc.body().parent();
        }
        removeEmpty(node);
        return node;
    }

    public Node domFilter(Node node, String select) {
        int parentLevel = 0;
        if (select != null && select.matches(".*!\\d+")) {
            int idx = select.lastIndexOf("!");
            parentLevel = Integer.parseInt(select.substring(idx + 1));
            select = select.substring(0, idx);
        }
        select = select == null ? "" : select.trim();
        String uri = "http://root.com";
        org.jsoup.nodes.Document doc = new org.jsoup.nodes.Document(uri);
        doc.append(node.outerHtml());

        if ((select != null) && (!select.isEmpty())) {
            Elements elements = doc.select(select);
            Element html = new Element(Tag.valueOf("html"), uri);
            for (Element e : elements) {
                for (int i = 0; i < parentLevel; i++) {
                    e = e.parent();
                }
                html.appendChild(e);
            }
            node = html;
        } else {
            node = doc.body().parent();
        }
        removeEmpty(node);
        return node;
    }

    public String domGetText(Node node) {
        return domGetText(node, "", true);
    }

    public String domGetText(Node node, String filtro, boolean childs) {
        filtro = filtro == null ? "" : filtro.trim();
        org.jsoup.nodes.Document doc = new org.jsoup.nodes.Document("http://root.com");
        doc.append(node.outerHtml());
        Elements elements = filtro.equals("") ? doc.children() : doc.select(filtro);
        StringBuilder sb = new StringBuilder();
        for (Element e : elements) {
            if (childs) {
                sb.append(e.text()).append("\n");
            } else {
                List<Node> list = e.childNodes();
                boolean first = true;
                for (Node n : list) {
                    if (first) {
                        first = false;
                    } else {
                        sb.append("\n");
                    }
                    if ((n instanceof TextNode)) {
                        sb.append(((TextNode) n).text());
                    }
                }
            }
        }
        return sb.toString().trim();
    }

    public boolean domExist(Node node, String filter) {
        filter = filter == null ? "" : filter.trim();
        org.jsoup.nodes.Document doc = new org.jsoup.nodes.Document("http://root.com");
        doc.append(node.outerHtml());
        Elements elements = doc.select(filter);
        return !elements.isEmpty();
    }

    //-----------------------------------------------------
    //			                  funciones time
    public static void delay(long step, long error) {
        try {
            double delay = step + Math.random() * error;
            Thread.sleep((long) delay);
        } catch (InterruptedException e) {
        }
    }

    public static void delay(long step) {
        try {
            double delay = step;
            Thread.sleep((long) delay);
        } catch (InterruptedException e) {
        }
    }
    
    private final SimpleDateFormat sdf= new SimpleDateFormat("yy/MM/dd HH:mm:ss");
    public void err(String msg, String context){
            System.err.println(sdf.format(new Date())+","+context+","+msg);
    }
    
    public void err(String msg){
        err(msg, "");
    }
    
    public void err(char msg, String context){
            System.err.println(msg);
    }
    
    public void err(char msg){
        err(msg, "");
    }
    
    
    public void log(String msg, String context){
            System.out.println(sdf.format(new Date())+","+context+","+msg);
    }
    
    public void log(String msg){
        err(msg, "");
    }
    
    public void log(char msg, String context){
            System.out.print(msg);
    }
    
    public void log(char msg){
        log(msg, "");
    }
    

    //-----------------------------------------------------
    //			               funciones mongo
    /**
     * crea un indice unique en una coleccion
     *
     * @param collection
     * @param keys
     * @param unique
     */
    public void dbIdxAscending(String collection, String[] keys, boolean unique) {
        IndexOptions indexOptions = new IndexOptions().unique(unique);
        DB.getCollection(collection).createIndex(Indexes.ascending(keys), indexOptions);
    }

    /**
     * crea un indice unique en una coleccion
     *
     * @param collection
     * @param keys
     * @param unique
     */
    public void dbIdxDescending(String collection, String[] keys, boolean unique) {
        IndexOptions indexOptions = new IndexOptions().unique(unique);
        DB.getCollection(collection).createIndex(Indexes.descending(keys), indexOptions);
    }

    /**
     * crea un indice unique en una coleccion
     *
     * @param collection
     * @param keys
     * @param unique
     */
    public void dbIdxText(String collection, String keys, boolean unique) {
        IndexOptions indexOptions = new IndexOptions().unique(unique);
        DB.getCollection(collection).createIndex(Indexes.text(keys), indexOptions);
    }

    /**
     * crea un indice unique en una coleccion
     *
     * @param collection
     * @param keys
     * @param unique
     */
    public void dbIdxGeo(String collection, String[] keys, boolean unique) {
        IndexOptions indexOptions = new IndexOptions().unique(unique);
        DB.getCollection(collection).createIndex(Indexes.geo2dsphere(keys), indexOptions);
    }

    public MongoCollection dbGetCollection(String collection) {
        return DB.getCollection(collection);
    }

    public void dbPutDoc(String collection, Document document) {
        dbGetCollection(collection).insertOne(document);
    }

    public void dbPutDoc(String collection, Object[] values) {
        Document document = dbNewDoc(values);
        dbPutDoc(collection, document);
    }

    public boolean dbUpdate(String collectionName, Object[] key, Object[] values) {
        MongoCollection<Document> collection = dbGetCollection(collectionName);
        try {

            Bson filter = null;
            for (int i = 0; i < key.length; i = i + 2) {
                Bson thisFilter = Filters.eq((String) key[i], key[i + 1]);
                filter = filter == null ? thisFilter : Filters.and(filter, thisFilter);
            }

            UpdateResult result = collection.updateOne(filter, new Document("$set", dbNewDoc(values)));
            return result.getMatchedCount() == 1;
        } catch (Exception e) {
            err(e.getLocalizedMessage());
            return false;
        }

    }

    public boolean dbUpdate(String collectionName, String id, Object idVal, Document document) {
        MongoCollection<Document> collection = dbGetCollection(collectionName);
        try {
            Bson filter = Filters.eq(id, idVal);
            UpdateResult result = collection.updateOne(filter, new Document("$set", document));
            return result.getMatchedCount() == 1;
        } catch (Exception e) {
            err(e.getLocalizedMessage());
            return false;
        }

    }

    public Document dbNewDoc(Object[] values) {
        Document document = new Document();
        if (values != null) {
            for (int i = 0; i < values.length; i += 2) {
                document.put((String) values[i], values[(i + 1)]);
            }
        }
        return document;
    }

    public FindIterable dbGetDoc(String collection) {
        return dbGetCollection(collection).find();
    }

    public boolean dbExistDoc(String collection, Document docKey) {
        return dbGetCollection(collection).find(docKey).iterator().hasNext();
    }

    public boolean dbExistDoc(String collection, Object[] key) {
        return dbGetDoc(collection, dbNewDoc(key)).iterator().hasNext();
    }

    public FindIterable dbGetDoc(String collection, Document docKey) {
        return dbGetCollection(collection).find(docKey);
    }

    public FindIterable dbGetDoc(String collection, Object[] key) {
        return dbGetDoc(collection, dbNewDoc(key));
    }

    public void dbDelDoc(String collection, Document docKey) {
        dbGetCollection(collection).deleteOne(docKey);
    }

    public void dbDelDoc(String collection, Object[] key) {
        dbGetCollection(collection).deleteOne(dbNewDoc(key));
    }

    private void removeEmpty(Node node) {
        HashSet<Node> delete = new HashSet<>();
        for (Node n : node.childNodes()) {
            if (n instanceof TextNode) {
                if (((TextNode) n).getWholeText().trim().isEmpty()) {
                    delete.add(n);
                } else if (n instanceof Element) {
                    String tag = ((Element) n).nodeName();
                    if (tag.equals("br")) {
                        delete.add(n);
                    }
                }
            } else if (n instanceof Element) {
                String tag = ((Element) n).nodeName();
                if (tag.equals("br")) {
                    delete.add(n);
                } else {
                    removeEmpty(n);
                }
            }
        }
        for (Node n : delete) {
            n.remove();
        }
    }

    private final HashMap<Character, String> htmlEncodeChars = new HashMap<>();

    {

        // Special characters for HTML
        htmlEncodeChars.put('\u0026', "&amp;");
        htmlEncodeChars.put('\u003C', "&lt;");
        htmlEncodeChars.put('\u003E', "&gt;");
        htmlEncodeChars.put('\u0022', "&quot;");

        htmlEncodeChars.put('\u0152', "&OElig;");
        htmlEncodeChars.put('\u0153', "&oelig;");
        htmlEncodeChars.put('\u0160', "&Scaron;");
        htmlEncodeChars.put('\u0161', "&scaron;");
        htmlEncodeChars.put('\u0178', "&Yuml;");
        htmlEncodeChars.put('\u02C6', "&circ;");
        htmlEncodeChars.put('\u02DC', "&tilde;");
        htmlEncodeChars.put('\u2002', "&ensp;");
        htmlEncodeChars.put('\u2003', "&emsp;");
        htmlEncodeChars.put('\u2009', "&thinsp;");
        htmlEncodeChars.put('\u200C', "&zwnj;");
        htmlEncodeChars.put('\u200D', "&zwj;");
        htmlEncodeChars.put('\u200E', "&lrm;");
        htmlEncodeChars.put('\u200F', "&rlm;");
        htmlEncodeChars.put('\u2013', "&ndash;");
        htmlEncodeChars.put('\u2014', "&mdash;");
        htmlEncodeChars.put('\u2018', "&lsquo;");
        htmlEncodeChars.put('\u2019', "&rsquo;");
        htmlEncodeChars.put('\u201A', "&sbquo;");
        htmlEncodeChars.put('\u201C', "&ldquo;");
        htmlEncodeChars.put('\u201D', "&rdquo;");
        htmlEncodeChars.put('\u201E', "&bdquo;");
        htmlEncodeChars.put('\u2020', "&dagger;");
        htmlEncodeChars.put('\u2021', "&Dagger;");
        htmlEncodeChars.put('\u2030', "&permil;");
        htmlEncodeChars.put('\u2039', "&lsaquo;");
        htmlEncodeChars.put('\u203A', "&rsaquo;");
        htmlEncodeChars.put('\u20AC', "&euro;");

        // Character entity references for ISO 8859-1 characters
        htmlEncodeChars.put('\u00A0', "&nbsp;");
        htmlEncodeChars.put('\u00A1', "&iexcl;");
        htmlEncodeChars.put('\u00A2', "&cent;");
        htmlEncodeChars.put('\u00A3', "&pound;");
        htmlEncodeChars.put('\u00A4', "&curren;");
        htmlEncodeChars.put('\u00A5', "&yen;");
        htmlEncodeChars.put('\u00A6', "&brvbar;");
        htmlEncodeChars.put('\u00A7', "&sect;");
        htmlEncodeChars.put('\u00A8', "&uml;");
        htmlEncodeChars.put('\u00A9', "&copy;");
        htmlEncodeChars.put('\u00AA', "&ordf;");
        htmlEncodeChars.put('\u00AB', "&laquo;");
        htmlEncodeChars.put('\u00AC', "&not;");
        htmlEncodeChars.put('\u00AD', "&shy;");
        htmlEncodeChars.put('\u00AE', "&reg;");
        htmlEncodeChars.put('\u00AF', "&macr;");
        htmlEncodeChars.put('\u00B0', "&deg;");
        htmlEncodeChars.put('\u00B1', "&plusmn;");
        htmlEncodeChars.put('\u00B2', "&sup2;");
        htmlEncodeChars.put('\u00B3', "&sup3;");
        htmlEncodeChars.put('\u00B4', "&acute;");
        htmlEncodeChars.put('\u00B5', "&micro;");
        htmlEncodeChars.put('\u00B6', "&para;");
        htmlEncodeChars.put('\u00B7', "&middot;");
        htmlEncodeChars.put('\u00B8', "&cedil;");
        htmlEncodeChars.put('\u00B9', "&sup1;");
        htmlEncodeChars.put('\u00BA', "&ordm;");
        htmlEncodeChars.put('\u00BB', "&raquo;");
        htmlEncodeChars.put('\u00BC', "&frac14;");
        htmlEncodeChars.put('\u00BD', "&frac12;");
        htmlEncodeChars.put('\u00BE', "&frac34;");
        htmlEncodeChars.put('\u00BF', "&iquest;");
        htmlEncodeChars.put('\u00C0', "&Agrave;");
        htmlEncodeChars.put('\u00C1', "&Aacute;");
        htmlEncodeChars.put('\u00C2', "&Acirc;");
        htmlEncodeChars.put('\u00C3', "&Atilde;");
        htmlEncodeChars.put('\u00C4', "&Auml;");
        htmlEncodeChars.put('\u00C5', "&Aring;");
        htmlEncodeChars.put('\u00C6', "&AElig;");
        htmlEncodeChars.put('\u00C7', "&Ccedil;");
        htmlEncodeChars.put('\u00C8', "&Egrave;");
        htmlEncodeChars.put('\u00C9', "&Eacute;");
        htmlEncodeChars.put('\u00CA', "&Ecirc;");
        htmlEncodeChars.put('\u00CB', "&Euml;");
        htmlEncodeChars.put('\u00CC', "&Igrave;");
        htmlEncodeChars.put('\u00CD', "&Iacute;");
        htmlEncodeChars.put('\u00CE', "&Icirc;");
        htmlEncodeChars.put('\u00CF', "&Iuml;");
        htmlEncodeChars.put('\u00D0', "&ETH;");
        htmlEncodeChars.put('\u00D1', "&Ntilde;");
        htmlEncodeChars.put('\u00D2', "&Ograve;");
        htmlEncodeChars.put('\u00D3', "&Oacute;");
        htmlEncodeChars.put('\u00D4', "&Ocirc;");
        htmlEncodeChars.put('\u00D5', "&Otilde;");
        htmlEncodeChars.put('\u00D6', "&Ouml;");
        htmlEncodeChars.put('\u00D7', "&times;");
        htmlEncodeChars.put('\u00D8', "&Oslash;");
        htmlEncodeChars.put('\u00D9', "&Ugrave;");
        htmlEncodeChars.put('\u00DA', "&Uacute;");
        htmlEncodeChars.put('\u00DB', "&Ucirc;");
        htmlEncodeChars.put('\u00DC', "&Uuml;");
        htmlEncodeChars.put('\u00DD', "&Yacute;");
        htmlEncodeChars.put('\u00DE', "&THORN;");
        htmlEncodeChars.put('\u00DF', "&szlig;");
        htmlEncodeChars.put('\u00E0', "&agrave;");
        htmlEncodeChars.put('\u00E1', "&aacute;");
        htmlEncodeChars.put('\u00E2', "&acirc;");
        htmlEncodeChars.put('\u00E3', "&atilde;");
        htmlEncodeChars.put('\u00E4', "&auml;");
        htmlEncodeChars.put('\u00E5', "&aring;");
        htmlEncodeChars.put('\u00E6', "&aelig;");
        htmlEncodeChars.put('\u00E7', "&ccedil;");
        htmlEncodeChars.put('\u00E8', "&egrave;");
        htmlEncodeChars.put('\u00E9', "&eacute;");
        htmlEncodeChars.put('\u00EA', "&ecirc;");
        htmlEncodeChars.put('\u00EB', "&euml;");
        htmlEncodeChars.put('\u00EC', "&igrave;");
        htmlEncodeChars.put('\u00ED', "&iacute;");
        htmlEncodeChars.put('\u00EE', "&icirc;");
        htmlEncodeChars.put('\u00EF', "&iuml;");
        htmlEncodeChars.put('\u00F0', "&eth;");
        htmlEncodeChars.put('\u00F1', "&ntilde;");
        htmlEncodeChars.put('\u00F2', "&ograve;");
        htmlEncodeChars.put('\u00F3', "&oacute;");
        htmlEncodeChars.put('\u00F4', "&ocirc;");
        htmlEncodeChars.put('\u00F5', "&otilde;");
        htmlEncodeChars.put('\u00F6', "&ouml;");
        htmlEncodeChars.put('\u00F7', "&divide;");
        htmlEncodeChars.put('\u00F8', "&oslash;");
        htmlEncodeChars.put('\u00F9', "&ugrave;");
        htmlEncodeChars.put('\u00FA', "&uacute;");
        htmlEncodeChars.put('\u00FB', "&ucirc;");
        htmlEncodeChars.put('\u00FC', "&uuml;");
        htmlEncodeChars.put('\u00FD', "&yacute;");
        htmlEncodeChars.put('\u00FE', "&thorn;");
        htmlEncodeChars.put('\u00FF', "&yuml;");

        // Mathematical, Greek and Symbolic characters for HTML
        htmlEncodeChars.put('\u0192', "&fnof;");
        htmlEncodeChars.put('\u0391', "&Alpha;");
        htmlEncodeChars.put('\u0392', "&Beta;");
        htmlEncodeChars.put('\u0393', "&Gamma;");
        htmlEncodeChars.put('\u0394', "&Delta;");
        htmlEncodeChars.put('\u0395', "&Epsilon;");
        htmlEncodeChars.put('\u0396', "&Zeta;");
        htmlEncodeChars.put('\u0397', "&Eta;");
        htmlEncodeChars.put('\u0398', "&Theta;");
        htmlEncodeChars.put('\u0399', "&Iota;");
        htmlEncodeChars.put('\u039A', "&Kappa;");
        htmlEncodeChars.put('\u039B', "&Lambda;");
        htmlEncodeChars.put('\u039C', "&Mu;");
        htmlEncodeChars.put('\u039D', "&Nu;");
        htmlEncodeChars.put('\u039E', "&Xi;");
        htmlEncodeChars.put('\u039F', "&Omicron;");
        htmlEncodeChars.put('\u03A0', "&Pi;");
        htmlEncodeChars.put('\u03A1', "&Rho;");
        htmlEncodeChars.put('\u03A3', "&Sigma;");
        htmlEncodeChars.put('\u03A4', "&Tau;");
        htmlEncodeChars.put('\u03A5', "&Upsilon;");
        htmlEncodeChars.put('\u03A6', "&Phi;");
        htmlEncodeChars.put('\u03A7', "&Chi;");
        htmlEncodeChars.put('\u03A8', "&Psi;");
        htmlEncodeChars.put('\u03A9', "&Omega;");
        htmlEncodeChars.put('\u03B1', "&alpha;");
        htmlEncodeChars.put('\u03B2', "&beta;");
        htmlEncodeChars.put('\u03B3', "&gamma;");
        htmlEncodeChars.put('\u03B4', "&delta;");
        htmlEncodeChars.put('\u03B5', "&epsilon;");
        htmlEncodeChars.put('\u03B6', "&zeta;");
        htmlEncodeChars.put('\u03B7', "&eta;");
        htmlEncodeChars.put('\u03B8', "&theta;");
        htmlEncodeChars.put('\u03B9', "&iota;");
        htmlEncodeChars.put('\u03BA', "&kappa;");
        htmlEncodeChars.put('\u03BB', "&lambda;");
        htmlEncodeChars.put('\u03BC', "&mu;");
        htmlEncodeChars.put('\u03BD', "&nu;");
        htmlEncodeChars.put('\u03BE', "&xi;");
        htmlEncodeChars.put('\u03BF', "&omicron;");
        htmlEncodeChars.put('\u03C0', "&pi;");
        htmlEncodeChars.put('\u03C1', "&rho;");
        htmlEncodeChars.put('\u03C2', "&sigmaf;");
        htmlEncodeChars.put('\u03C3', "&sigma;");
        htmlEncodeChars.put('\u03C4', "&tau;");
        htmlEncodeChars.put('\u03C5', "&upsilon;");
        htmlEncodeChars.put('\u03C6', "&phi;");
        htmlEncodeChars.put('\u03C7', "&chi;");
        htmlEncodeChars.put('\u03C8', "&psi;");
        htmlEncodeChars.put('\u03C9', "&omega;");
        htmlEncodeChars.put('\u03D1', "&thetasym;");
        htmlEncodeChars.put('\u03D2', "&upsih;");
        htmlEncodeChars.put('\u03D6', "&piv;");
        htmlEncodeChars.put('\u2022', "&bull;");
        htmlEncodeChars.put('\u2026', "&hellip;");
        htmlEncodeChars.put('\u2032', "&prime;");
        htmlEncodeChars.put('\u2033', "&Prime;");
        htmlEncodeChars.put('\u203E', "&oline;");
        htmlEncodeChars.put('\u2044', "&frasl;");
        htmlEncodeChars.put('\u2118', "&weierp;");
        htmlEncodeChars.put('\u2111', "&image;");
        htmlEncodeChars.put('\u211C', "&real;");
        htmlEncodeChars.put('\u2122', "&trade;");
        htmlEncodeChars.put('\u2135', "&alefsym;");
        htmlEncodeChars.put('\u2190', "&larr;");
        htmlEncodeChars.put('\u2191', "&uarr;");
        htmlEncodeChars.put('\u2192', "&rarr;");
        htmlEncodeChars.put('\u2193', "&darr;");
        htmlEncodeChars.put('\u2194', "&harr;");
        htmlEncodeChars.put('\u21B5', "&crarr;");
        htmlEncodeChars.put('\u21D0', "&lArr;");
        htmlEncodeChars.put('\u21D1', "&uArr;");
        htmlEncodeChars.put('\u21D2', "&rArr;");
        htmlEncodeChars.put('\u21D3', "&dArr;");
        htmlEncodeChars.put('\u21D4', "&hArr;");
        htmlEncodeChars.put('\u2200', "&forall;");
        htmlEncodeChars.put('\u2202', "&part;");
        htmlEncodeChars.put('\u2203', "&exist;");
        htmlEncodeChars.put('\u2205', "&empty;");
        htmlEncodeChars.put('\u2207', "&nabla;");
        htmlEncodeChars.put('\u2208', "&isin;");
        htmlEncodeChars.put('\u2209', "&notin;");
        htmlEncodeChars.put('\u220B', "&ni;");
        htmlEncodeChars.put('\u220F', "&prod;");
        htmlEncodeChars.put('\u2211', "&sum;");
        htmlEncodeChars.put('\u2212', "&minus;");
        htmlEncodeChars.put('\u2217', "&lowast;");
        htmlEncodeChars.put('\u221A', "&radic;");
        htmlEncodeChars.put('\u221D', "&prop;");
        htmlEncodeChars.put('\u221E', "&infin;");
        htmlEncodeChars.put('\u2220', "&ang;");
        htmlEncodeChars.put('\u2227', "&and;");
        htmlEncodeChars.put('\u2228', "&or;");
        htmlEncodeChars.put('\u2229', "&cap;");
        htmlEncodeChars.put('\u222A', "&cup;");
        htmlEncodeChars.put('\u222B', "&int;");
        htmlEncodeChars.put('\u2234', "&there4;");
        htmlEncodeChars.put('\u223C', "&sim;");
        htmlEncodeChars.put('\u2245', "&cong;");
        htmlEncodeChars.put('\u2248', "&asymp;");
        htmlEncodeChars.put('\u2260', "&ne;");
        htmlEncodeChars.put('\u2261', "&equiv;");
        htmlEncodeChars.put('\u2264', "&le;");
        htmlEncodeChars.put('\u2265', "&ge;");
        htmlEncodeChars.put('\u2282', "&sub;");
        htmlEncodeChars.put('\u2283', "&sup;");
        htmlEncodeChars.put('\u2284', "&nsub;");
        htmlEncodeChars.put('\u2286', "&sube;");
        htmlEncodeChars.put('\u2287', "&supe;");
        htmlEncodeChars.put('\u2295', "&oplus;");
        htmlEncodeChars.put('\u2297', "&otimes;");
        htmlEncodeChars.put('\u22A5', "&perp;");
        htmlEncodeChars.put('\u22C5', "&sdot;");
        htmlEncodeChars.put('\u2308', "&lceil;");
        htmlEncodeChars.put('\u2309', "&rceil;");
        htmlEncodeChars.put('\u230A', "&lfloor;");
        htmlEncodeChars.put('\u230B', "&rfloor;");
        htmlEncodeChars.put('\u2329', "&lang;");
        htmlEncodeChars.put('\u232A', "&rang;");
        htmlEncodeChars.put('\u25CA', "&loz;");
        htmlEncodeChars.put('\u2660', "&spades;");
        htmlEncodeChars.put('\u2663', "&clubs;");
        htmlEncodeChars.put('\u2665', "&hearts;");
        htmlEncodeChars.put('\u2666', "&diams;");
    }

    public String textEncodeHtml(String source) {
        return encode(source, htmlEncodeChars);
    }

    private String encode(String source, HashMap<Character, String> encodingTable) {
        if (null == source) {
            return null;
        }

        if (null == encodingTable) {
            return source;
        }

        StringBuffer encoded_string = null;
        char[] string_to_encode_array = source.toCharArray();
        int last_match = -1;
        int difference = 0;

        for (int i = 0; i < string_to_encode_array.length; i++) {
            char char_to_encode = string_to_encode_array[i];

            if (encodingTable.containsKey(char_to_encode)) {
                if (null == encoded_string) {
                    encoded_string = new StringBuffer(source.length());
                }
                difference = i - (last_match + 1);
                if (difference > 0) {
                    encoded_string.append(string_to_encode_array, last_match + 1, difference);
                }
                encoded_string.append(encodingTable.get(char_to_encode));
                last_match = i;
            }
        }

        if (null == encoded_string) {
            return source;
        } else {
            difference = string_to_encode_array.length - (last_match + 1);
            if (difference > 0) {
                encoded_string.append(string_to_encode_array, last_match + 1, difference);
            }
            return encoded_string.toString();
        }
    }

}
