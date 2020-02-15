export interface ILocation {
  id?: number;
  longitude?: number;
  latitude?: number;
  town?: string;
  street?: string;
  postal?: string;
  houseNumber?: number;
  country?: string;
  additional?: string;
}

export class Location implements ILocation {
  constructor(
    public id?: number,
    public longitude?: number,
    public latitude?: number,
    public town?: string,
    public street?: string,
    public postal?: string,
    public houseNumber?: number,
    public country?: string,
    public additional?: string
  ) {}
}
