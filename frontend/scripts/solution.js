function checkVariable(input) {
    switch (typeof input) {
        case "string":
            return "string";

        case "number":
            return "number";

        case "boolean":
            return "boolean";

        case "bigint":
            return "bigint";

        case "undefined":
            return "undefined";

        case "object":
            return "object";

        default:
            return "unknown type";
    }
}
// const readline = require("readline");

// const rl = readline.createInterface({
//   input: process.stdin,
//   output: process.stdout
// });

// rl.question("Enter any datatype: ", function(input) {
//   console.log(checkVariable(input))
//   rl.close();
// });

console.log("---------------Problem 1----------------\n");

const prompt = require("prompt-sync")();

let input = prompt("Enter any datatype: ");

if (input === "") {
    input = undefined;
}
else if (input === "true") {
    input = true;
}
else if (input === "false") {
    input = false;
}
else if (/^\d+n$/.test(input)) {
    input = BigInt(input.slice(0, -1));
}
else if (/^\{.*\}$/.test(input) || /^\[.*\]$/.test(input)) {
    try {
        input = JSON.parse(input);
    } catch (e) {

    }
}
else if (!isNaN(input)) {
    input = Number(input);
}

console.log(checkVariable(input));
console.log("\n");



function generateIDs(count) {

    const ids = [0, 1, 2, 3, 4, 5, 6, 7];
    const result = [];

    for (let i = 0; i <= count; i++) {

        if (i === 5) {
            continue;
        }

        const id = `ID-${ids[i]}`;
        result.push(id);
    }

    return result;
}

console.log("---------------Problem 2----------------\n");
console.log(generateIDs(7));
console.log("\n");



// try
function calculateTotal(...numbers) {
    try {
        if (!numbers.every(num => typeof num === "number")) {
            throw new TypeError("Invalid input: All arguments must be numbers");
        }


        return numbers.reduce((sum, num) => sum + num, 0);
    } catch (error) {
        console.log(error.message);
    }
    //
    // if (!numbers.every(num => typeof num === "number")) {
    //     throw new TypeError("Invalid input: All arguments must be numbers");
    // }
    // return numbers.reduce((sum, num) => sum + num, 0);
}

console.log("---------------Problem 3----------------\n");
console.log("Sum:", calculateTotal(5, 10, 15));
console.log("Sum:", calculateTotal("1", "7", "3", "4"));
console.log("\n");
// try {
//     console.log("Sum:", calculateTotal(5, 10, 15));
//     console.log("Sum:", calculateTotal("1", "7", "3", "4"));
// } catch (error) {
//     console.log(error.message);
// }


// function getScorers(playerList) {

//     let topScorers = [];

//     for (let i = 0; i < playerList.length; i++) {
//         let player = playerList[i];

//         if (player.score > 8) {
//             topScorers.push(player.name);
            
//         }
        
//     }
//     return topScorers.join(",");


    
// }

function getTopScorers(playerList) {
    return playerList
        .filter(player => player.score > 8)       
        .map(player => player.name)               
        .join(', ');                              
}
const playerList =[
    {"name": "Jona", "score": 10},
    {"name": "rocky", "score": 9},
    {"name": "kyle", "score": 5},
    {"name": "kai", "score": 6},
    {"name": "junel", "score": 8},

    {"name": "judy", "score": 7},
    {"name": "shella", "score": 6},
    {"name": "nicole", "score": 5},
    {"name": "jennie", "score": 5},
    {"name": "julius", "score": 1}
]

console.log("---------------Problem 4----------------\n");
console.log(getTopScorers(playerList));
console.log("\n");

function safeDivide(a,b) {

    try {
        if (b===0) {
            throw new Error(`Cannot divide by zero`);
            
        }

        return a / b;
    } catch (error) {
        return error.message;
        
    }finally{
        console.log("Operation attempted");
    }
    
}

console.log("---------------Problem 5----------------\n");

console.log(safeDivide(9,0));
console.log("\n");
console.log(safeDivide(9,3));