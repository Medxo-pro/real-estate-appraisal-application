
// Define a Map where keys are strings and values are 2D arrays of numbers
const map = new Map<string, string[][]>();

const data = [];
for (let i = 0; i < 40; i++) {  
  data.push([`${i+1}`, `${i-1}`, `${i*3}`]);
}

const longData = [];
for (let i = 0; i < 40; i++) {
  longData.push([
    `${i+1}`, `${i-1}`, `${i*3}`, `${i}`, `${i/2}`, `${i*4}`, 
    `${i}`, `${i}`, `${i}`, `${i}`, `${i}`, `${i}`
  ]);
}

map.set("Table A", [
  ["Label 1", "Label 2", "Label 3"],
  ...data, // ... for append
]);

map.set("Table B", [
  ["7", "8"],
  ["9", "10"],
  ["11", "12"]
]);

map.set("Table E", [
  ["Label 1", "Label 2", "Label 3", "Label 4", "Label 5", "Label 6", "Label 7", "Label 8", "Label 9", "Label 10"],
  ["1", "2.5", "3.14", "4.0", "5", "6.75", "7.1", "8.9", "9.0", "10"],
  ["11.5", "12", "13.2", "14.8", "15", "16.33", "17", "18.01", "19", "20.5"],
  ["21", "22.6", "23.8", "24", "25.4", "26", "27.2", "28", "29.99", "30"],
  ["31.11", "32", "33", "34.7", "35", "36.3", "37", "38", "39.25", "40"],
  ["41", "42.5", "43.8", "44", "45.75", "46", "47", "48.2", "49.4", "50"],
  ["51.6", "52", "53.3", "54.1", "55", "56.7", "57", "58", "59", "60.0"],
  ["61", "62.25", "63", "64.8", "65", "66", "67.5", "68", "69.9", "70"],
  ["71.3", "72", "73.7", "74.4", "75", "76", "77.2", "78.0", "79", "80"],
  ["81.1", "82", "83.8", "84.6", "85", "86", "87.5", "88", "89.2", "90"],
  ["91", "92.0", "93.3", "94.1", "95", "96.8", "97", "98", "99", "100.0"],
  ["101", "102.5", "103.0", "104.7", "105", "106.1", "107", "108", "109.5", "110"],
  ["111.2", "112", "113", "114.3", "115", "116.7", "117", "118", "119.0", "120"],
  ["121", "122.5", "123.8", "124.1", "125", "126", "127.3", "128.0", "129.9", "130"],
  ["131", "132.6", "133", "134.5", "135", "136", "137.8", "138", "139.1", "140"],
  ["141", "142.0", "143.3", "144.6", "145", "146", "147.2", "148", "149", "150.0"],
  ["151.5", "152", "153", "154.4", "155", "156.1", "157", "158", "159", "160"],
  ["161", "162.3", "163", "164.2", "165", "166", "167.7", "168", "169.5", "170"],
  ["171.8", "172", "173.1", "174.0", "175", "176.5", "177", "178", "179", "180"],
  ["181", "182.4", "183", "184.6", "185", "186.0", "187", "188", "189", "190"],
  ["191", "192.7", "193", "194.2", "195", "196", "197.3", "198", "199.5", "200"]
]);

map.set("Student Record", [
  ["Student Name", "GPA", "Grade Lvl", "Age", "Score"],
  ["Mehdi", "4.0", "8", "15", "99"],
  ["Aisha", "3.8", "8", "14", "92"],
  ["Liam", "3.5", "8", "15", "85"],
  ["Emma", "3.7", "8", "14", "100"],
  ["Noah", "3.9", "8", "15", "94"],
  ["Olivia", "3.7", "8", "14", "88"],
  ["James", "4.0", "8", "15", "97"],
  ["Sophia", "3.6", "8", "14", "90"],
  ["Lucas", "4.1", "8", "15", "95"],
  ["Mia", "3.8", "8", "14", "91"],
  ["Ethan", "3.9", "8", "15", "96"],
  ["Isabella", "4.3", "8", "14", "98"],
  ["Alexander", "3.5", "8", "15", "84"],
  ["Amelia", "4.0", "8", "14", "93"],
]);

map.set("Student Record State", [
  ["Student Name", "GPA", "Grade Lvl", "Age", "Score", "State"],
  ["Mehdi", "4.0", "8", "15", "99", "RI"],
  ["Aisha", "3.8", "8", "14", "92", "MA"],
  ["Liam", "3.5", "8", "15", "85", "TX"],
  ["Emma", "3.7", "8", "14", "100", "RI"],
  ["Noah", "3.9", "8", "15", "94", "RI"],
  ["Olivia", "3.7", "8", "14", "88", "TX"],
  ["James", "4.0", "8", "15", "97", "RI"],
  ["Sophia", "3.6", "8", "14", "90", "MA"],
  ["Lucas", "4.1", "8", "15", "95", "RI"],
  ["Mia", "3.8", "8", "14", "91", "NY"],
  ["Ethan", "3.9", "8", "15", "96", "NY"],
  ["Isabella", "4.3", "8", "14", "98", "RI"],
  ["Alexander", "3.5", "8", "15", "84", "RI"],
  ["Amelia", "4.0", "8", "14", "93", "RI"],
]);

map.set("Plant Record", [
  ["Plant Name", "Type", "Height (cm)", "Growth Rate (cm/year)", "Water Needs", "Sunlight"],
  ["Rose", "Flowering", "100", "15", "Moderate", "Full Sun"],
  ["Oak", "Tree", "500", "30", "Low", "Full Sun"],
  ["Cactus", "Succulent", "30", "5", "Low", "Full Sun"],
  ["Bamboo", "Grass", "300", "100", "High", "Partial Sun"],
  ["Tulip", "Flowering", "50", "20", "Moderate", "Full Sun"],
  ["Fern", "Foliage", "40", "10", "High", "Shade"],
  ["Lily", "Flowering", "60", "15", "Moderate", "Full Sun"],
  ["Maple", "Tree", "300", "25", "Low", "Full Sun"],
  ["Orchid", "Flowering", "25", "10", "High", "Indirect Sun"],
  ["Basil", "Herb", "30", "20", "Moderate", "Full Sun"],
]);

map.set("Lost of Data", [
  ["1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12"],
  ...longData, 
]);

/**
 * Gets the name of the table (the key of the hashmap) and returns the value (the list of lists of values)
 *
 * @returns a 2d array (list of lists)
 *
 */
export function getTable(label:string) {
    const matrixA = map.get(label);
    return matrixA;
}