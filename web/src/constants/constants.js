export const NO_VALUE = "-1";

export const GAS = ["CO", "SO2", "other"];

export const GAS_CONCENTRATION ={
    "CO":{
        "Good":[0, 2],
        "Standard":[2, 6],
        "Alert":[6, 9],
        "Warning":[9, 15],
        "Emergency":[15, 21],
        "Significant Harm":[21, 24]
    },
    "SO2":{
        "Good":[0, 2],
        "Standard":[2, 9],
        "Alert":[9, 19],
        "Warning":[19, 38],
        "Emergency":[38, 50],
        "Significant Harm":[50, 63]
    }
}

export const GAS_CONCENTRATION_KEY = [
    "Good",
    "Standard",
    "Alert",
    "Warning",
    "Emergency",
    "Significant Harm"
]

export const TIME = [0.5, 1]; //h