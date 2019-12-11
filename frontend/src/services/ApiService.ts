import axios from "axios";

class ApiService {
    static async loadRides(lat: Number, lon: Number) {
        // return axios(
        //     {method: "get",
        //             url: "http://localhost:8080/rides",
        //             params: {
        //                 lon: lon,
        //                 lat: lat,
        //                 max: 100000
        //             }}
        // );

        return fetch("http://localhost:8080/rides?lon=" + lon + "&lat=" + lat + "&max=" + 100000).then(r => r.json());
    }

    static async loadIncidents(lat: Number, lon: Number) {
        // return axios(
        //     {method: "get",
        //         url: "http://localhost:8080/incidents",
        //         params: {
        //             lon: lon,
        //             lat: lat,
        //             max: 100000
        //         }}
        // );

        return fetch("http://localhost:8080/incidents?lon=" + lon + "&lat=" + lat + "&max=" + 100000).then(r => r.json());
    }

    static async startDataProcessing() {
        return (await axios.get("/api/actions/startDataProcessing")).data;
    }
}

export { ApiService };
