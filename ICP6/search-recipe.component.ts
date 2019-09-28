import {Component, ElementRef, OnInit, ViewChild} from '@angular/core';
import {HttpClient} from '@angular/common/http';

@Component({
  selector: 'app-search-recipe',
  templateUrl: './search-recipe.component.html',
  styleUrls: ['./search-recipe.component.css']
})
export class SearchRecipeComponent implements OnInit {
  @ViewChild('recipe', { static: true }) recipes: ElementRef;
  @ViewChild('place', { static: true }) places: ElementRef;
  recipeValue: any;
  placeValue: any;
  venueList = [];
  recipeList = [];

  currentLat: any;
  currentLong: any;
  geolocationPosition: any;



  constructor(private _http: HttpClient) {
  }

  ngOnInit() {

    window.navigator.geolocation.getCurrentPosition(
      position => {
        this.geolocationPosition = position;
        this.currentLat = position.coords.latitude;
        this.currentLong = position.coords.longitude;
      });
  }

  getVenues() {
    this.recipeValue = this.recipes.nativeElement.value;
    this.placeValue = this.places.nativeElement.value;
    if (this.recipeValue === null) {
    } else {
      this._http.get('Https://cors-anywhere.herokuapp.com/https://api.edamam.com/search?q=' + this.recipeValue + '&app_id=cc21dbfb&app_key=c1a3f5866b9a117ee57b3693ddb83c29&from=0&to=3')
        .subscribe((data: any) => {
          for (var i = 0; i < data.hits.length; i++) {

            this.recipeList[i] = {
              "name": data.hits[i].recipe.label,
              "url": data.hits[i].recipe.url,
              "icon": data.hits[i].recipe.image
            };
          }
        });
    }


    if (this.placeValue != null && this.placeValue != "" && this.recipeValue != null && this.recipeValue != "") {
      this._http.get("https://api.foursquare.com/v2/venues/search" +
        "?client_id=NYRIK5HODE0QZOU2KK2PWNFAUTETZWIOINPE1S1N4C2FQVMN" +
        "&client_secret=1AGG1YVBQFL2NQJ0YMNSKW1V0KOEI4N3BZPW4SKXC0FOLVAN" +
        "&v=20160215&limit=5" +
        "&near=" + this.placeValue +
        "&query=" + this.recipeValue)
        .subscribe((data: any) => {
          for (var i = 0; i < data.response.venues.length; i++) {
            this.venueList[i] = {
              "name": data.response.venues[i].name,
              "id": data.response.venues[i].id,
              "location": data.response.venues[i].location
            };
            console.log(this.venueList[i]);

          }

        })
    }

  }
}
