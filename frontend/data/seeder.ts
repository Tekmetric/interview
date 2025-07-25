import {faker, fakerES} from '@faker-js/faker';
import {promises as fs} from "fs"
import bcrypt from "bcryptjs"

let id = 1

function createCoffeeEntity() {
    return {
        id: "" + id++,
        farmName: faker.helpers.arrayElement(["Las", "Finca", "Collective"]) + " " + fakerES.person.lastName(),
        price: faker.commerce.price({min: 5, max: 30, symbol: "$"}),
        altitude: faker.number.int({min: 1500, max: 2500}),
        tasteNotes: faker.helpers.arrayElements(faker.helpers.multiple(faker.food.fruit, {count: 5}).concat(faker.helpers.multiple(faker.food.spice, {count: 5})), {
            min: 3,
            max: 7
        }),
        description: faker.commerce.productDescription(),
        userId: '1'
    };
}

function createAdminUser() {
    return {
        id: "1",
        name: "Andrei",
        email: "andrei@gmail.com",
        password: bcrypt.hashSync("password"),
    }
}


const coffees = faker.helpers.multiple(createCoffeeEntity, {count: 100})
const data = JSON.stringify({coffees, users: [createAdminUser()]}, null, 2);

fs.writeFile("data/db.json", data, "utf8")