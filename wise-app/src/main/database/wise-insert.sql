-- 0. User Roles
INSERT INTO userrole (id, description, created, last_update, is_active) VALUES
                                                                           (0, 'Standard User', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, true),
                                                                           (1, 'Staff', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, true),
                                                                           (2, 'Admin', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, true);

-- 1. Age Ratings
INSERT INTO AgeRating (name, description, age) VALUES
                                              ('T', 'Suitable for all', 0),
                                              ('6+', 'Suitable for children aged 6 and over', 6),
                                              ('12+', 'Suitable for children aged 12 and over', 12),
                                              ('15+', 'Suitable for people aged 15 and over', 15),
                                              ('18+', 'Adults only', 18);

-- 2. Genres
INSERT INTO Genre (name) VALUES
                             ('Action'), ('Adventure'), ('Animation'), ('Comedy'), ('Crime'),
                             ('Documentary'), ('Drama'), ('Family'), ('Fantasy'), ('Horror'),
                             ('Musical'), ('Sci-Fi'), ('Thriller'), ('Western'), ('Romance'), ('Mystery');

-- 3. Movie Roles
INSERT INTO MovieRole (name) VALUES
                                 ('Actor'), ('Director');

-- 4. Categories
INSERT INTO Category (name, description, is_active) VALUES
                                             ('Home Premiere', 'Straight from the Cinema to your home.', true),
                                             ('Most Popular', 'The trending titles everyone is watching right now.', true),
                                             ('Movie Deals', 'Great films at a special discounted price.', true),
                                             ('Kids & Family', 'Entertainment for the little ones and the whole family.', true),
                                             ('Must-See', 'Critics choice and award-winning masterpieces.', true),
                                             ('Cult Classics', 'Movies that shaped the history of cinema.', true),
                                             ('Oscars 2025', 'Movies nominated for the 97th Academy Awards.', true);

-- 5. Languages
INSERT INTO Language (name) VALUES
                                ('English'), ('Italian'), ('Spanish'), ('Korean'), ('Japanese');

-- 6. Workers
INSERT INTO Worker (name, surname) VALUES
                                       ('Jon', 'Chu'), ('Ariana', 'Grande'), ('James', 'Gunn'), ('David', 'Corenswet'),
                                       ('Christopher', 'Nolan'), ('Cillian', 'Murphy'), ('Denis', 'Villeneuve'), ('Timothee', 'Chalamet'),
                                       ('Phil', 'Lord'), ('Ryan', 'Gosling'), ('Aaron', 'Horvath'), ('Chris', 'Pratt'),
                                       ('Greta', 'Gerwig'), ('Margot', 'Robbie'), ('Luc', 'Besson'), ('Caleb', 'Landry Jones'),
                                       ('Garth', 'Jennings'), ('Matthew', 'McConaughey'), ('Jared', 'Hess'), ('Jack', 'Black'),
                                       ('Leonardo', 'DiCaprio'), ('Christian', 'Bale'), ('Lana', 'Wachowski'), ('Keanu', 'Reeves'),
                                       ('David', 'Fincher'), ('Brad', 'Pitt'), ('Quentin', 'Tarantino'), ('John', 'Travolta'),
                                       ('Robert', 'Zemeckis'), ('Tom', 'Hanks'), ('Frank', 'Darabont'), ('Morgan', 'Freeman'),
                                       ('Francis Ford', 'Coppola'), ('Marlon', 'Brando'), ('Peter', 'Jackson'), ('Elijah', 'Wood'),
                                       ('Hayao', 'Miyazaki'), ('Rumi', 'Hiiragi'), ('Bong', 'Joon Ho'), ('Song', 'Kang-ho'),
                                       ('Anthony', 'Russo'), ('Robert', 'Downey Jr.'), ('Ridley', 'Scott'), ('Russell', 'Crowe'),
                                       ('Roger', 'Allers'), ('Matthew', 'Broderick'), ('James', 'Cameron'), ('Kate', 'Winslet'),
                                       ('Steven', 'Spielberg'), ('Sam', 'Neill'), ('Michael', 'J. Fox'), ('Martin', 'Scorsese'),
                                       ('Ray', 'Liotta'), ('Jonathan', 'Demme'), ('Jodie', 'Foster'), ('Sigourney', 'Weaver'),
                                       ('Stanley', 'Kubrick'), ('Jack', 'Nicholson'), ('Damien', 'Chazelle'), ('Miles', 'Teller'),
                                       ('George', 'Miller'), ('Tom', 'Hardy'), ('Pete', 'Docter'), ('Amy', 'Poehler'),
                                       ('Lee', 'Unkrich'), ('Anthony', 'Gonzalez'), ('John', 'Lasseter'), ('Andrew', 'Stanton'),
                                       ('Ben', 'Burtt'), ('Jamie', 'Foxx'), ('Peter', 'Weir'), ('Jim', 'Carrey'),
                                       ('Daniel', 'Kwan'), ('Michelle', 'Yeoh'), ('Bob', 'Persichetti'), ('Shameik', 'Moore'),
                                       ('Todd', 'Phillips'), ('Joaquin', 'Phoenix'), ('Sam', 'Worthington'), ('Mel', 'Gibson'),
                                       ('M.', 'Night Shyamalan'), ('Bruce', 'Willis'), ('Gus', 'Van Sant'), ('Matt', 'Damon'),
                                       ('Joel', 'Coen'), ('Jeff', 'Bridges'), ('Emma', 'Stone'), ('Amy', 'Adams'),
                                       ('Jesse', 'Eisenberg');

-- 7. Movies
INSERT INTO Movie (title, synopsis, release_year, running_time, rating, base_price_eur, poster_pic, trailer, is_active) VALUES
                                                                                                                            ('Wicked: For Good', 'The untold story of the witches of Oz, focusing on Elphaba and Glinda.', 2024, 160, 2, 19.99, 'Wicked For Good.jpg', '6COmYeLsz4c', true),
                                                                                                                            ('Oppenheimer', 'The story of American scientist J. Robert Oppenheimer and his role in the development of the atomic bomb.', 2023, 180, 4, 9.99, 'Oppenheimer.jpg', 'uYPbbksJxIg', true),
                                                                                                                            ('Superman', 'The journey of Superman to reconcile his Kryptonian heritage with his human upbringing.', 2025, 145, 3, 15.99, 'Superman.jpg', 'uhUht6vAsMY', true),
                                                                                                                            ('Dune: Part Two', 'Paul Atreides unites with Chani and the Fremen while on a warpath of revenge.', 2024, 166, 3, 12.99, 'Dune Part Two.jpg', 'Way9Dexny3w', true),
                                                                                                                            ('Project Hail Mary', 'An astronaut wakes up on a spaceship with no memory and must save humanity.', 2026, 130, 3, 19.99, 'Project Hail Mary.jpg', 'm08TxIsFTRI', true),
                                                                                                                            ('The Super Mario Bros. Movie', 'A plumber named Mario travels through an underground labyrinth with his brother.', 2023, 92, 1, 7.99, 'The Super Mario Bros. Movie.jpg', 'TnGl01FkMMo', true),
                                                                                                                            ('Barbie', 'Barbie and Ken are having the time of their lives in the colorful and seemingly perfect world.', 2023, 114, 2, 8.99, 'Barbie.jpg', 'pBk4NYhWNMM', true),
                                                                                                                            ('Dracula', 'A modern reimagining of the classic vampire tale.', 2025, 120, 5, 14.99, 'Dracula.jpg', 'w05UztF-3xY', true),
                                                                                                                            ('Sing 2', 'Buster Moon and his all-star cast of performers prepare to launch their most dazzling stage extravaganza.', 2021, 110, 1, 4.99, 'Sing 2.jpg', 'EPZu5MA2uqI', true),
                                                                                                                            ('A Minecraft Movie', 'Four misfits are suddenly pulled through a mysterious portal into a bizarre cubic wonderland.', 2025, 101, 1, 14.99, 'A Minecraft Movie.jpg', 'wJO_vIDZn-I', true),
                                                                                                                            ('Inception', 'A thief who steals corporate secrets through the use of dream-sharing technology is given the inverse task of planting an idea into the mind of a C.E.O.', 2010, 148, 3, 3.99, 'Inception.jpg', 'YoHD9XEInc0', true),
                                                                                                                            ('The Dark Knight', 'When the menace known as the Joker wreaks havoc and chaos on the people of Gotham, Batman must accept one of the greatest psychological and physical tests of his ability to fight injustice.', 2008, 152, 3, 3.99, 'The Dark Knight.jpg', 'EXeTwQWrcwY', true),
                                                                                                                            ('Interstellar', 'A team of explorers travel through a wormhole in space in an attempt to ensure humanity''s survival.', 2014, 169, 3, 4.99, 'Interstellar.jpg', 'zSWdZVtXT7E', true),
                                                                                                                            ('The Matrix', 'When a beautiful stranger leads computer hacker Neo to a forbidding underworld, he discovers the shocking truth--the life he knows is the elaborate deception of an evil cyber-intelligence.', 1999, 136, 4, 3.99, 'The Matrix.jpg', 'vKQi3bBA1y8', true),
                                                                                                                            ('Fight Club', 'An insomniac office worker and a devil-may-care soap maker form an underground fight club that evolves into much more.', 1999, 139, 5, 4.99, 'Fight Club.jpg', 'qtRKdVHc-cE', true),
                                                                                                                            ('Pulp Fiction', 'The lives of two mob hitmen, a boxer, a gangster and his wife, and a pair of diner bandits intertwine in four tales of violence and redemption.', 1994, 154, 5, 4.99, 'Pulp Fiction.jpg', 's7EdQ4FqbhY', true),
                                                                                                                            ('Forrest Gump', 'The presidencies of Kennedy and Johnson, the Vietnam War, the Watergate scandal and other historical events unfold from the perspective of an Alabama man with an IQ of 75, whose only desire is to be reunited with his childhood sweetheart.', 1994, 142, 3, 3.99, 'Forrest Gump.jpg', 'bLvqoHBptjg', true),
                                                                                                                            ('The Shawshank Redemption', 'Two imprisoned men bond over a number of years, finding solace and eventual redemption through acts of common decency.', 1994, 142, 4, 3.99, 'The Shawshank Redemption.jpg', '6hB3S9bIaco', true),
                                                                                                                            ('The Godfather', 'The aging patriarch of an organized crime dynasty transfers control of his clandestine empire to his reluctant son.', 1972, 175, 5, 4.99, 'The Godfather.jpg', 'sY1S34973zA', true),
                                                                                                                            ('The Lord of the Rings: The Fellowship of the Ring', 'A meek Hobbit from the Shire and eight companions set out on a journey to destroy the powerful One Ring and save Middle-earth from the Dark Lord Sauron.', 2001, 178, 3, 4.99, 'The Lord of the Rings The Fellowship of the Ring.jpg', 'aStYWD25fAQ', true),
                                                                                                                            ('Spirited Away', 'During her family''s move to the suburbs, a sullen 10-year-old girl wanders into a world ruled by gods, witches, and spirits, and where humans are changed into beasts.', 2001, 125, 2, 5.99, 'Spirited Away.jpg', 'ByXuk9QqQkk', true),
                                                                                                                            ('Parasite', 'Greed and class discrimination threaten the newly formed symbiotic relationship between the wealthy Park family and the destitute Kim clan.', 2019, 132, 5, 6.99, 'Parasite.jpg', '5xH0HfJHsaY', true),
                                                                                                                            ('Avengers: Endgame', 'After the devastating events of Infinity War, the Avengers assemble once more in order to reverse Thanos'' actions and restore balance to the universe.', 2019, 181, 3, 7.99, 'Avengers Endgame.jpg', 'TcMBFSGVi1c', true),
                                                                                                                            ('Gladiator', 'A former Roman General sets out to exact vengeance against the corrupt emperor who murdered his family and sent him into slavery.', 2000, 155, 4, 3.99, 'Gladiator.jpg', 'owK1qxDselE', true),
                                                                                                                            ('The Lion King', 'Lion prince Simba and his father are targeted by his bitter uncle, who wants to ascend the throne himself.', 1994, 88, 1, 4.99, 'The Lion King.jpg', 'lFzVJEksoDY', true),
                                                                                                                            ('Titanic', 'A seventeen-year-old aristocrat falls in love with a kind but poor artist aboard the luxurious, ill-fated R.M.S. Titanic.', 1997, 194, 3, 4.99, 'Titanic.jpg', 'kVrqfYjkTdQ', true),
                                                                                                                            ('Jurassic Park', 'A pragmatic paleontologist touring an almost complete theme park on an island in Central America is tasked with protecting a couple of kids after a power failure causes the park''s cloned dinosaurs to run loose.', 1993, 127, 3, 3.99, 'Jurassic Park.jpg', 'lc0UehYemQA', true),
                                                                                                                            ('Back to the Future', 'Marty McFly, a 17-year-old high school student, is accidentally sent thirty years into the past in a time-traveling DeLorean invented by his close friend, the eccentric scientist Doc Brown.', 1985, 116, 2, 3.99, 'Back to the Future.jpg', 'qvsgGtivCgs', true),
                                                                                                                            ('Goodfellas', 'The story of Henry Hill and his life in the mob, covering his relationship with his wife Karen Hill and his mob partners Jimmy Conway and Tommy DeVito in the Italian-American crime syndicate.', 1990, 145, 5, 4.99, 'Goodfellas.jpg', '2ilzidi_J8Q', true),
                                                                                                                            ('Se7en', 'Two detectives, a rookie and a veteran, hunt a serial killer who uses the seven deadly sins as his motives.', 1995, 127, 5, 4.99, 'Se7en.jpg', 'znmZoVkCjpI', true),
                                                                                                                            ('The Silence of the Lambs', 'A young F.B.I. cadet must receive the help of an incarcerated and manipulative cannibal killer to help catch another serial killer, a madman who skins his victims.', 1991, 118, 5, 4.99, 'The Silence of the Lambs.jpg', 'W6Mm8Sbe__o', true),
                                                                                                                            ('Alien', 'The crew of a commercial spacecraft encounter a deadly lifeform after investigating an unknown transmission.', 1979, 117, 4, 3.99, 'Alien.jpg', 'jQ5lPt9edzQ', true),
                                                                                                                            ('The Shining', 'A family heads to an isolated hotel for the winter where a sinister presence influences the father into violence, while his psychic son sees horrific forebodings from both past and future.', 1980, 146, 5, 4.99, 'The Shining.jpg', 'S014oGZiSdI', true),
                                                                                                                            ('Whiplash', 'A promising young drummer enrolls at a cut-throat music conservatory where his dreams of greatness are mentored by an instructor who will stop at nothing to realize a student''s potential.', 2014, 106, 4, 5.99, 'Whiplash.jpg', '7d_jQycdQGo', true),
                                                                                                                            ('The Departed', 'An undercover cop and a mole in the police attempt to identify each other while infiltrating an Irish gang in South Boston.', 2006, 151, 5, 4.99, 'The Departed.jpg', 'iojhqm0JTW4', true),
                                                                                                                            ('Mad Max: Fury Road', 'In a post-apocalyptic wasteland, a woman rebels against a tyrannical ruler in search for her homeland with the aid of a group of female prisoners, a psychotic worshiper, and a drifter named Max.', 2015, 120, 4, 5.99, 'Mad Max Fury Road.jpg', 'hEJnMQG9ev8', true),
                                                                                                                            ('Inside Out', 'After young Riley is uprooted from her Midwest life and moved to San Francisco, her emotions - Joy, Fear, Anger, Disgust and Sadness - conflict on how best to navigate a new city, house, and school.', 2015, 95, 1, 4.99, 'Inside Out.jpg', 'yRUAzGQ3nSY', true),
                                                                                                                            ('Coco', 'Aspiring musician Miguel, confronted with his family''s ancestral ban on music, enters the Land of the Dead to find his great-great-grandfather, a legendary singer.', 2017, 105, 1, 5.99, 'Coco.jpg', 'Rvr68u6k5sI', true),
                                                                                                                            ('Toy Story', 'A cowboy doll is profoundly threatened and jealous when a new spaceman figure supplants him as top toy in a boy''s room.', 1995, 81, 1, 3.99, 'Toy Story.jpg', 'v-PjgYDrg70', true),
                                                                                                                            ('WALL-E', 'In the distant future, a small waste-collecting robot inadvertently embarks on a space journey that will ultimately decide the fate of mankind.', 2008, 98, 1, 4.99, 'WALL-E.jpg', 'CZ1CATNbXg0', true),
                                                                                                                            ('Django Unchained', 'With the help of a German bounty-hunter, a freed slave sets out to rescue his wife from a brutal plantation-owner in Mississippi.', 2012, 165, 5, 4.99, 'Django Unchained.jpg', '0fUCuvNlOCg', true),
                                                                                                                            ('Inglourious Basterds', 'In Nazi-occupied France during World War II, a plan to assassinate Nazi leaders by a group of Jewish U.S. soldiers coincides with a theatre owner''s vengeful plans for the same.', 2009, 153, 5, 4.99, 'Inglourious Basterds.jpg', 'KnrRy6kSFF0', true),
                                                                                                                            ('The Truman Show', 'An insurance salesman discovers his whole life is actually a reality TV show.', 1998, 103, 2, 3.99, 'The Truman Show.jpg', 'dlnmQbPGuls', true),
                                                                                                                            ('Everything Everywhere All at Once', 'A middle-aged Chinese immigrant is swept up into an insane adventure in which she alone can save existence by exploring other universes and connecting with the lives she could have led.', 2022, 139, 4, 7.99, 'Everything Everywhere All at Once.jpg', 'wxN1T1uxQ2g', true),
                                                                                                                            ('Spider-Man: Into the Spider-Verse', 'Teen Miles Morales becomes the Spider-Man of his universe, and must join with five spider-powered individuals from other dimensions to stop a threat for all realities.', 2018, 117, 2, 5.99, 'Spider-Man Into the Spider-Verse.jpg', 'tg52up16eq0', true),
                                                                                                                            ('Joker', 'In Gotham City, mentally troubled comedian Arthur Fleck is disregarded and mistreated by society. He then embarks on a downward spiral of revolution and bloody crime.', 2019, 122, 5, 6.99, 'Joker.jpg', 'zAGVQLHvwOY', true),
                                                                                                                            ('Avatar', 'A paraplegic Marine dispatched to the moon Pandora on a unique mission becomes torn between following his orders and protecting the world he feels is his home.', 2009, 162, 3, 4.99, 'Avatar.jpg', '5PSNL1qE6VY', true),
                                                                                                                            ('The Avengers', 'Earth''s mightiest heroes must come together and learn to fight as a team if they are going to stop the mischievous Loki and his alien army from enslaving humanity.', 2012, 143, 3, 4.99, 'The Avengers.jpg', 'eOrNdBpGMv8', true),
                                                                                                                            ('Braveheart', 'Scottish warrior William Wallace leads his countrymen in a rebellion to free his homeland from the tyranny of King Edward I of England.', 1995, 178, 5, 4.99, 'Braveheart.jpg', '1NJO0jxBtMo', true),
                                                                                                                            ('The Green Mile', 'The lives of guards on Death Row are affected by one of their charges: a black man accused of child murder and rape, yet who has a mysterious gift.', 1999, 189, 5, 4.99, 'The Green Mile.jpg', 'Ki4haFrqSrw', true),
                                                                                                                            ('The Sixth Sense', 'A boy who communicates with spirits seeks the help of a disheartened child psychologist.', 1999, 107, 4, 3.99, 'The Sixth Sense.jpg', '3-ZP95NF_Wk', true),
                                                                                                                            ('Good Will Hunting', 'Will Hunting, a janitor at M.I.T., has a gift for mathematics, but needs help from a psychologist to find direction in his life.', 1997, 126, 4, 4.99, 'Good Will Hunting.jpg', 'DyiPHC_VQFY', true),
                                                                                                                            ('The Big Lebowski', 'Jeff "The Dude" Lebowski, mistaken for a millionaire of the same name, seeks restitution for his ruined rug and enlists his bowling buddies to help get it.', 1998, 117, 5, 3.99, 'The Big Lebowski.jpg', 'cd-go0oBF4Y', true),
                                                                                                                            ('Catch Me If You Can', 'Barely 21 yet, Frank is a skilled forger who has passed as a doctor, lawyer and pilot. FBI agent Carl, becomes obsessed with tracking down the con man.', 2002, 141, 3, 3.99, 'Catch Me If You Can.jpg', 's-7pyIxz8Qg', true),
                                                                                                                            ('The Wolf of Wall Street', 'Based on the true story of Jordan Belfort, from his rise to a wealthy stock-broker living the high life to his fall involving crime, corruption and the federal government.', 2013, 180, 5, 5.99, 'The Wolf of Wall Street.jpg', 'iszwuX1AK6A', true),
                                                                                                                            ('Shutter Island', 'In 1954, a U.S. Marshal investigates the disappearance of a murderer who escaped from a hospital for the criminally insane.', 2010, 138, 4, 4.99, 'Shutter Island.jpg', '5iaYLCiq5RM', true),
                                                                                                                            ('Blade Runner 2049', 'Young Blade Runner K''s discovery of a long-buried secret leads him to track down former Blade Runner Rick Deckard, who''s been missing for thirty years.', 2017, 164, 4, 5.99, 'Blade Runner 2049.jpg', 'gCcx85zbxz4', true),
                                                                                                                            ('The Social Network', 'As Harvard student Mark Zuckerberg creates the social networking site that would become known as Facebook, he is sued by the twins who claimed he stole their idea.', 2010, 120, 3, 4.99, 'The Social Network.jpg', 'lB95KLmpLR4', true),
                                                                                                                            ('Arrival', 'A linguist works with the military to communicate with alien lifeforms after twelve mysterious spacecraft appear around the world.', 2016, 116, 3, 5.99, 'Arrival.jpg', 'tFMo3UJ4B4g', true),
                                                                                                                            ('La La Land', 'While navigating their careers in Los Angeles, a pianist and an actress fall in love while attempting to reconcile their aspirations for the future.', 2016, 128, 3, 5.99, 'La La Land.jpg', 'waTDxRZ93Qc', true);

-- 8. Link Categories
INSERT INTO HasCategory (movie_id, category_id)
SELECT movie_id, (SELECT category_id FROM Category WHERE name = 'Home Premiere') FROM Movie WHERE title IN ('Wicked: For Good', 'Project Hail Mary', 'Superman', 'Dracula', 'A Minecraft Movie');

INSERT INTO HasCategory (movie_id, category_id)
SELECT movie_id, (SELECT category_id FROM Category WHERE name = 'Must-See') FROM Movie WHERE title IN ('Oppenheimer', 'Dune: Part Two', 'Inception', 'The Godfather', 'Parasite', 'Everything Everywhere All at Once');

INSERT INTO HasCategory (movie_id, category_id)
SELECT movie_id, (SELECT category_id FROM Category WHERE name = 'Kids & Family') FROM Movie WHERE title IN ('The Super Mario Bros. Movie', 'Sing 2', 'The Lion King', 'Inside Out', 'Coco', 'Toy Story', 'WALL-E', 'Spirited Away');

INSERT INTO HasCategory (movie_id, category_id)
SELECT movie_id, (SELECT category_id FROM Category WHERE name = 'Cult Classics') FROM Movie WHERE title IN ('The Matrix', 'Fight Club', 'Pulp Fiction', 'The Big Lebowski', 'Alien', 'The Shining');

INSERT INTO HasCategory (movie_id, category_id)
SELECT movie_id, (SELECT category_id FROM Category WHERE name = 'Most Popular') FROM Movie WHERE title IN ('Barbie', 'Avengers: Endgame', 'The Dark Knight', 'Interstellar');

INSERT INTO HasCategory (movie_id, category_id)
SELECT movie_id, (SELECT category_id FROM Category WHERE name = 'Oscars 2025') FROM Movie WHERE title IN ('Dune: Part Two');

-- 9. Link Credits (Directors and Actors)
INSERT INTO Credit (movie_id, worker_id, role_id) VALUES
-- Directors
((SELECT movie_id FROM Movie WHERE title = 'Wicked: For Good'), (SELECT worker_id FROM Worker WHERE surname = 'Chu'), (SELECT role_id FROM MovieRole WHERE name = 'Director')),
((SELECT movie_id FROM Movie WHERE title = 'Oppenheimer'), (SELECT worker_id FROM Worker WHERE surname = 'Nolan'), (SELECT role_id FROM MovieRole WHERE name = 'Director')),
((SELECT movie_id FROM Movie WHERE title = 'Superman'), (SELECT worker_id FROM Worker WHERE surname = 'Gunn'), (SELECT role_id FROM MovieRole WHERE name = 'Director')),
((SELECT movie_id FROM Movie WHERE title = 'Dune: Part Two'), (SELECT worker_id FROM Worker WHERE surname = 'Villeneuve'), (SELECT role_id FROM MovieRole WHERE name = 'Director')),
((SELECT movie_id FROM Movie WHERE title = 'Project Hail Mary'), (SELECT worker_id FROM Worker WHERE surname = 'Lord'), (SELECT role_id FROM MovieRole WHERE name = 'Director')),
((SELECT movie_id FROM Movie WHERE title = 'The Super Mario Bros. Movie'), (SELECT worker_id FROM Worker WHERE surname = 'Horvath'), (SELECT role_id FROM MovieRole WHERE name = 'Director')),
((SELECT movie_id FROM Movie WHERE title = 'Barbie'), (SELECT worker_id FROM Worker WHERE surname = 'Gerwig'), (SELECT role_id FROM MovieRole WHERE name = 'Director')),
((SELECT movie_id FROM Movie WHERE title = 'Dracula'), (SELECT worker_id FROM Worker WHERE surname = 'Besson'), (SELECT role_id FROM MovieRole WHERE name = 'Director')),
((SELECT movie_id FROM Movie WHERE title = 'Sing 2'), (SELECT worker_id FROM Worker WHERE surname = 'Jennings'), (SELECT role_id FROM MovieRole WHERE name = 'Director')),
((SELECT movie_id FROM Movie WHERE title = 'A Minecraft Movie'), (SELECT worker_id FROM Worker WHERE surname = 'Hess'), (SELECT role_id FROM MovieRole WHERE name = 'Director')),
((SELECT movie_id FROM Movie WHERE title = 'Inception'), (SELECT worker_id FROM Worker WHERE surname = 'Nolan'), (SELECT role_id FROM MovieRole WHERE name = 'Director')),
((SELECT movie_id FROM Movie WHERE title = 'The Dark Knight'), (SELECT worker_id FROM Worker WHERE surname = 'Nolan'), (SELECT role_id FROM MovieRole WHERE name = 'Director')),
((SELECT movie_id FROM Movie WHERE title = 'The Matrix'), (SELECT worker_id FROM Worker WHERE surname = 'Wachowski'), (SELECT role_id FROM MovieRole WHERE name = 'Director')),
((SELECT movie_id FROM Movie WHERE title = 'Fight Club'), (SELECT worker_id FROM Worker WHERE surname = 'Fincher'), (SELECT role_id FROM MovieRole WHERE name = 'Director')),
((SELECT movie_id FROM Movie WHERE title = 'Pulp Fiction'), (SELECT worker_id FROM Worker WHERE surname = 'Tarantino'), (SELECT role_id FROM MovieRole WHERE name = 'Director')),
((SELECT movie_id FROM Movie WHERE title = 'Forrest Gump'), (SELECT worker_id FROM Worker WHERE surname = 'Zemeckis'), (SELECT role_id FROM MovieRole WHERE name = 'Director')),
((SELECT movie_id FROM Movie WHERE title = 'The Shawshank Redemption'), (SELECT worker_id FROM Worker WHERE surname = 'Darabont'), (SELECT role_id FROM MovieRole WHERE name = 'Director')),
((SELECT movie_id FROM Movie WHERE title = 'The Godfather'), (SELECT worker_id FROM Worker WHERE surname = 'Coppola'), (SELECT role_id FROM MovieRole WHERE name = 'Director')),
((SELECT movie_id FROM Movie WHERE title = 'The Lord of the Rings: The Fellowship of the Ring'), (SELECT worker_id FROM Worker WHERE surname = 'Jackson'), (SELECT role_id FROM MovieRole WHERE name = 'Director')),
((SELECT movie_id FROM Movie WHERE title = 'Spirited Away'), (SELECT worker_id FROM Worker WHERE surname = 'Miyazaki'), (SELECT role_id FROM MovieRole WHERE name = 'Director')),
((SELECT movie_id FROM Movie WHERE title = 'Parasite'), (SELECT worker_id FROM Worker WHERE surname = 'Joon Ho'), (SELECT role_id FROM MovieRole WHERE name = 'Director')),
((SELECT movie_id FROM Movie WHERE title = 'Avengers: Endgame'), (SELECT worker_id FROM Worker WHERE surname = 'Russo'), (SELECT role_id FROM MovieRole WHERE name = 'Director')),
((SELECT movie_id FROM Movie WHERE title = 'Gladiator'), (SELECT worker_id FROM Worker WHERE surname = 'Scott'), (SELECT role_id FROM MovieRole WHERE name = 'Director')),
((SELECT movie_id FROM Movie WHERE title = 'The Lion King'), (SELECT worker_id FROM Worker WHERE surname = 'Allers'), (SELECT role_id FROM MovieRole WHERE name = 'Director')),
((SELECT movie_id FROM Movie WHERE title = 'Titanic'), (SELECT worker_id FROM Worker WHERE surname = 'Cameron'), (SELECT role_id FROM MovieRole WHERE name = 'Director')),
((SELECT movie_id FROM Movie WHERE title = 'Jurassic Park'), (SELECT worker_id FROM Worker WHERE surname = 'Spielberg'), (SELECT role_id FROM MovieRole WHERE name = 'Director')),
((SELECT movie_id FROM Movie WHERE title = 'Back to the Future'), (SELECT worker_id FROM Worker WHERE surname = 'Zemeckis'), (SELECT role_id FROM MovieRole WHERE name = 'Director')),
((SELECT movie_id FROM Movie WHERE title = 'Goodfellas'), (SELECT worker_id FROM Worker WHERE surname = 'Scorsese'), (SELECT role_id FROM MovieRole WHERE name = 'Director')),
((SELECT movie_id FROM Movie WHERE title = 'Se7en'), (SELECT worker_id FROM Worker WHERE surname = 'Fincher'), (SELECT role_id FROM MovieRole WHERE name = 'Director')),
((SELECT movie_id FROM Movie WHERE title = 'The Silence of the Lambs'), (SELECT worker_id FROM Worker WHERE surname = 'Demme'), (SELECT role_id FROM MovieRole WHERE name = 'Director')),
((SELECT movie_id FROM Movie WHERE title = 'Alien'), (SELECT worker_id FROM Worker WHERE surname = 'Scott'), (SELECT role_id FROM MovieRole WHERE name = 'Director')),
((SELECT movie_id FROM Movie WHERE title = 'The Shining'), (SELECT worker_id FROM Worker WHERE surname = 'Kubrick'), (SELECT role_id FROM MovieRole WHERE name = 'Director')),
((SELECT movie_id FROM Movie WHERE title = 'Whiplash'), (SELECT worker_id FROM Worker WHERE surname = 'Chazelle'), (SELECT role_id FROM MovieRole WHERE name = 'Director')),
((SELECT movie_id FROM Movie WHERE title = 'The Departed'), (SELECT worker_id FROM Worker WHERE surname = 'Scorsese'), (SELECT role_id FROM MovieRole WHERE name = 'Director')),
((SELECT movie_id FROM Movie WHERE title = 'Mad Max: Fury Road'), (SELECT worker_id FROM Worker WHERE surname = 'Miller'), (SELECT role_id FROM MovieRole WHERE name = 'Director')),
((SELECT movie_id FROM Movie WHERE title = 'Inside Out'), (SELECT worker_id FROM Worker WHERE surname = 'Docter'), (SELECT role_id FROM MovieRole WHERE name = 'Director')),
((SELECT movie_id FROM Movie WHERE title = 'Coco'), (SELECT worker_id FROM Worker WHERE surname = 'Unkrich'), (SELECT role_id FROM MovieRole WHERE name = 'Director')),
((SELECT movie_id FROM Movie WHERE title = 'Toy Story'), (SELECT worker_id FROM Worker WHERE surname = 'Lasseter'), (SELECT role_id FROM MovieRole WHERE name = 'Director')),
((SELECT movie_id FROM Movie WHERE title = 'WALL-E'), (SELECT worker_id FROM Worker WHERE surname = 'Stanton'), (SELECT role_id FROM MovieRole WHERE name = 'Director')),
((SELECT movie_id FROM Movie WHERE title = 'Django Unchained'), (SELECT worker_id FROM Worker WHERE surname = 'Tarantino'), (SELECT role_id FROM MovieRole WHERE name = 'Director')),
((SELECT movie_id FROM Movie WHERE title = 'Inglourious Basterds'), (SELECT worker_id FROM Worker WHERE surname = 'Tarantino'), (SELECT role_id FROM MovieRole WHERE name = 'Director')),
((SELECT movie_id FROM Movie WHERE title = 'The Truman Show'), (SELECT worker_id FROM Worker WHERE surname = 'Weir'), (SELECT role_id FROM MovieRole WHERE name = 'Director')),
((SELECT movie_id FROM Movie WHERE title = 'Everything Everywhere All at Once'), (SELECT worker_id FROM Worker WHERE surname = 'Kwan'), (SELECT role_id FROM MovieRole WHERE name = 'Director')),
((SELECT movie_id FROM Movie WHERE title = 'Spider-Man: Into the Spider-Verse'), (SELECT worker_id FROM Worker WHERE surname = 'Persichetti'), (SELECT role_id FROM MovieRole WHERE name = 'Director')),
((SELECT movie_id FROM Movie WHERE title = 'Joker'), (SELECT worker_id FROM Worker WHERE surname = 'Phillips'), (SELECT role_id FROM MovieRole WHERE name = 'Director')),
((SELECT movie_id FROM Movie WHERE title = 'Avatar'), (SELECT worker_id FROM Worker WHERE surname = 'Cameron'), (SELECT role_id FROM MovieRole WHERE name = 'Director')),
((SELECT movie_id FROM Movie WHERE title = 'The Avengers'), (SELECT worker_id FROM Worker WHERE surname = 'Russo'), (SELECT role_id FROM MovieRole WHERE name = 'Director')),
((SELECT movie_id FROM Movie WHERE title = 'Braveheart'), (SELECT worker_id FROM Worker WHERE surname = 'Gibson'), (SELECT role_id FROM MovieRole WHERE name = 'Director')),
((SELECT movie_id FROM Movie WHERE title = 'The Green Mile'), (SELECT worker_id FROM Worker WHERE surname = 'Darabont'), (SELECT role_id FROM MovieRole WHERE name = 'Director')),
((SELECT movie_id FROM Movie WHERE title = 'The Sixth Sense'), (SELECT worker_id FROM Worker WHERE surname = 'Night Shyamalan'), (SELECT role_id FROM MovieRole WHERE name = 'Director')),
((SELECT movie_id FROM Movie WHERE title = 'Good Will Hunting'), (SELECT worker_id FROM Worker WHERE surname = 'Van Sant'), (SELECT role_id FROM MovieRole WHERE name = 'Director')),
((SELECT movie_id FROM Movie WHERE title = 'The Big Lebowski'), (SELECT worker_id FROM Worker WHERE surname = 'Coen'), (SELECT role_id FROM MovieRole WHERE name = 'Director')),
((SELECT movie_id FROM Movie WHERE title = 'Catch Me If You Can'), (SELECT worker_id FROM Worker WHERE surname = 'Spielberg'), (SELECT role_id FROM MovieRole WHERE name = 'Director')),
((SELECT movie_id FROM Movie WHERE title = 'The Wolf of Wall Street'), (SELECT worker_id FROM Worker WHERE surname = 'Scorsese'), (SELECT role_id FROM MovieRole WHERE name = 'Director')),
((SELECT movie_id FROM Movie WHERE title = 'Shutter Island'), (SELECT worker_id FROM Worker WHERE surname = 'Scorsese'), (SELECT role_id FROM MovieRole WHERE name = 'Director')),
((SELECT movie_id FROM Movie WHERE title = 'Blade Runner 2049'), (SELECT worker_id FROM Worker WHERE surname = 'Villeneuve'), (SELECT role_id FROM MovieRole WHERE name = 'Director')),
((SELECT movie_id FROM Movie WHERE title = 'The Social Network'), (SELECT worker_id FROM Worker WHERE surname = 'Fincher'), (SELECT role_id FROM MovieRole WHERE name = 'Director')),
((SELECT movie_id FROM Movie WHERE title = 'Arrival'), (SELECT worker_id FROM Worker WHERE surname = 'Villeneuve'), (SELECT role_id FROM MovieRole WHERE name = 'Director')),
((SELECT movie_id FROM Movie WHERE title = 'La La Land'), (SELECT worker_id FROM Worker WHERE surname = 'Chazelle'), (SELECT role_id FROM MovieRole WHERE name = 'Director')),

-- Actors
((SELECT movie_id FROM Movie WHERE title = 'Wicked: For Good'), (SELECT worker_id FROM Worker WHERE surname = 'Grande'), (SELECT role_id FROM MovieRole WHERE name = 'Actor')),
((SELECT movie_id FROM Movie WHERE title = 'Oppenheimer'), (SELECT worker_id FROM Worker WHERE surname = 'Murphy'), (SELECT role_id FROM MovieRole WHERE name = 'Actor')),
((SELECT movie_id FROM Movie WHERE title = 'Superman'), (SELECT worker_id FROM Worker WHERE surname = 'Corenswet'), (SELECT role_id FROM MovieRole WHERE name = 'Actor')),
((SELECT movie_id FROM Movie WHERE title = 'Dune: Part Two'), (SELECT worker_id FROM Worker WHERE surname = 'Chalamet'), (SELECT role_id FROM MovieRole WHERE name = 'Actor')),
((SELECT movie_id FROM Movie WHERE title = 'Project Hail Mary'), (SELECT worker_id FROM Worker WHERE surname = 'Gosling'), (SELECT role_id FROM MovieRole WHERE name = 'Actor')),
((SELECT movie_id FROM Movie WHERE title = 'The Super Mario Bros. Movie'), (SELECT worker_id FROM Worker WHERE surname = 'Pratt'), (SELECT role_id FROM MovieRole WHERE name = 'Actor')),
((SELECT movie_id FROM Movie WHERE title = 'Barbie'), (SELECT worker_id FROM Worker WHERE surname = 'Robbie'), (SELECT role_id FROM MovieRole WHERE name = 'Actor')),
((SELECT movie_id FROM Movie WHERE title = 'Dracula'), (SELECT worker_id FROM Worker WHERE surname = 'Landry Jones'), (SELECT role_id FROM MovieRole WHERE name = 'Actor')),
((SELECT movie_id FROM Movie WHERE title = 'Sing 2'), (SELECT worker_id FROM Worker WHERE surname = 'McConaughey'), (SELECT role_id FROM MovieRole WHERE name = 'Actor')),
((SELECT movie_id FROM Movie WHERE title = 'A Minecraft Movie'), (SELECT worker_id FROM Worker WHERE surname = 'Black'), (SELECT role_id FROM MovieRole WHERE name = 'Actor')),
((SELECT movie_id FROM Movie WHERE title = 'Inception'), (SELECT worker_id FROM Worker WHERE surname = 'DiCaprio'), (SELECT role_id FROM MovieRole WHERE name = 'Actor')),
((SELECT movie_id FROM Movie WHERE title = 'The Dark Knight'), (SELECT worker_id FROM Worker WHERE surname = 'Bale'), (SELECT role_id FROM MovieRole WHERE name = 'Actor')),
((SELECT movie_id FROM Movie WHERE title = 'The Matrix'), (SELECT worker_id FROM Worker WHERE surname = 'Reeves'), (SELECT role_id FROM MovieRole WHERE name = 'Actor')),
((SELECT movie_id FROM Movie WHERE title = 'Fight Club'), (SELECT worker_id FROM Worker WHERE surname = 'Pitt'), (SELECT role_id FROM MovieRole WHERE name = 'Actor')),
((SELECT movie_id FROM Movie WHERE title = 'Pulp Fiction'), (SELECT worker_id FROM Worker WHERE surname = 'Travolta'), (SELECT role_id FROM MovieRole WHERE name = 'Actor')),
((SELECT movie_id FROM Movie WHERE title = 'Forrest Gump'), (SELECT worker_id FROM Worker WHERE surname = 'Hanks'), (SELECT role_id FROM MovieRole WHERE name = 'Actor')),
((SELECT movie_id FROM Movie WHERE title = 'The Shawshank Redemption'), (SELECT worker_id FROM Worker WHERE surname = 'Freeman'), (SELECT role_id FROM MovieRole WHERE name = 'Actor')),
((SELECT movie_id FROM Movie WHERE title = 'The Godfather'), (SELECT worker_id FROM Worker WHERE surname = 'Brando'), (SELECT role_id FROM MovieRole WHERE name = 'Actor')),
((SELECT movie_id FROM Movie WHERE title = 'The Lord of the Rings: The Fellowship of the Ring'), (SELECT worker_id FROM Worker WHERE surname = 'Wood'), (SELECT role_id FROM MovieRole WHERE name = 'Actor')),
((SELECT movie_id FROM Movie WHERE title = 'Spirited Away'), (SELECT worker_id FROM Worker WHERE surname = 'Hiiragi'), (SELECT role_id FROM MovieRole WHERE name = 'Actor')),
((SELECT movie_id FROM Movie WHERE title = 'Parasite'), (SELECT worker_id FROM Worker WHERE surname = 'Kang-ho'), (SELECT role_id FROM MovieRole WHERE name = 'Actor')),
((SELECT movie_id FROM Movie WHERE title = 'Avengers: Endgame'), (SELECT worker_id FROM Worker WHERE surname = 'Downey Jr.'), (SELECT role_id FROM MovieRole WHERE name = 'Actor')),
((SELECT movie_id FROM Movie WHERE title = 'Gladiator'), (SELECT worker_id FROM Worker WHERE surname = 'Crowe'), (SELECT role_id FROM MovieRole WHERE name = 'Actor')),
((SELECT movie_id FROM Movie WHERE title = 'The Lion King'), (SELECT worker_id FROM Worker WHERE surname = 'Broderick'), (SELECT role_id FROM MovieRole WHERE name = 'Actor')),
((SELECT movie_id FROM Movie WHERE title = 'Titanic'), (SELECT worker_id FROM Worker WHERE surname = 'Winslet'), (SELECT role_id FROM MovieRole WHERE name = 'Actor')),
((SELECT movie_id FROM Movie WHERE title = 'Jurassic Park'), (SELECT worker_id FROM Worker WHERE surname = 'Neill'), (SELECT role_id FROM MovieRole WHERE name = 'Actor')),
((SELECT movie_id FROM Movie WHERE title = 'Back to the Future'), (SELECT worker_id FROM Worker WHERE surname = 'J. Fox'), (SELECT role_id FROM MovieRole WHERE name = 'Actor')),
((SELECT movie_id FROM Movie WHERE title = 'Goodfellas'), (SELECT worker_id FROM Worker WHERE surname = 'Liotta'), (SELECT role_id FROM MovieRole WHERE name = 'Actor')),
((SELECT movie_id FROM Movie WHERE title = 'Se7en'), (SELECT worker_id FROM Worker WHERE surname = 'Pitt'), (SELECT role_id FROM MovieRole WHERE name = 'Actor')),
((SELECT movie_id FROM Movie WHERE title = 'The Silence of the Lambs'), (SELECT worker_id FROM Worker WHERE surname = 'Foster'), (SELECT role_id FROM MovieRole WHERE name = 'Actor')),
((SELECT movie_id FROM Movie WHERE title = 'Alien'), (SELECT worker_id FROM Worker WHERE surname = 'Weaver'), (SELECT role_id FROM MovieRole WHERE name = 'Actor')),
((SELECT movie_id FROM Movie WHERE title = 'The Shining'), (SELECT worker_id FROM Worker WHERE surname = 'Nicholson'), (SELECT role_id FROM MovieRole WHERE name = 'Actor')),
((SELECT movie_id FROM Movie WHERE title = 'Whiplash'), (SELECT worker_id FROM Worker WHERE surname = 'Teller'), (SELECT role_id FROM MovieRole WHERE name = 'Actor')),
((SELECT movie_id FROM Movie WHERE title = 'The Departed'), (SELECT worker_id FROM Worker WHERE surname = 'DiCaprio'), (SELECT role_id FROM MovieRole WHERE name = 'Actor')),
((SELECT movie_id FROM Movie WHERE title = 'Mad Max: Fury Road'), (SELECT worker_id FROM Worker WHERE surname = 'Hardy'), (SELECT role_id FROM MovieRole WHERE name = 'Actor')),
((SELECT movie_id FROM Movie WHERE title = 'Inside Out'), (SELECT worker_id FROM Worker WHERE surname = 'Poehler'), (SELECT role_id FROM MovieRole WHERE name = 'Actor')),
((SELECT movie_id FROM Movie WHERE title = 'Coco'), (SELECT worker_id FROM Worker WHERE surname = 'Gonzalez'), (SELECT role_id FROM MovieRole WHERE name = 'Actor')),
((SELECT movie_id FROM Movie WHERE title = 'Toy Story'), (SELECT worker_id FROM Worker WHERE surname = 'Hanks'), (SELECT role_id FROM MovieRole WHERE name = 'Actor')),
((SELECT movie_id FROM Movie WHERE title = 'WALL-E'), (SELECT worker_id FROM Worker WHERE surname = 'Burtt'), (SELECT role_id FROM MovieRole WHERE name = 'Actor')),
((SELECT movie_id FROM Movie WHERE title = 'Django Unchained'), (SELECT worker_id FROM Worker WHERE surname = 'Foxx'), (SELECT role_id FROM MovieRole WHERE name = 'Actor')),
((SELECT movie_id FROM Movie WHERE title = 'Inglourious Basterds'), (SELECT worker_id FROM Worker WHERE surname = 'Pitt'), (SELECT role_id FROM MovieRole WHERE name = 'Actor')),
((SELECT movie_id FROM Movie WHERE title = 'The Truman Show'), (SELECT worker_id FROM Worker WHERE surname = 'Carrey'), (SELECT role_id FROM MovieRole WHERE name = 'Actor')),
((SELECT movie_id FROM Movie WHERE title = 'Everything Everywhere All at Once'), (SELECT worker_id FROM Worker WHERE surname = 'Yeoh'), (SELECT role_id FROM MovieRole WHERE name = 'Actor')),
((SELECT movie_id FROM Movie WHERE title = 'Spider-Man: Into the Spider-Verse'), (SELECT worker_id FROM Worker WHERE surname = 'Moore'), (SELECT role_id FROM MovieRole WHERE name = 'Actor')),
((SELECT movie_id FROM Movie WHERE title = 'Joker'), (SELECT worker_id FROM Worker WHERE surname = 'Phoenix'), (SELECT role_id FROM MovieRole WHERE name = 'Actor')),
((SELECT movie_id FROM Movie WHERE title = 'Avatar'), (SELECT worker_id FROM Worker WHERE surname = 'Worthington'), (SELECT role_id FROM MovieRole WHERE name = 'Actor')),
((SELECT movie_id FROM Movie WHERE title = 'Braveheart'), (SELECT worker_id FROM Worker WHERE surname = 'Gibson'), (SELECT role_id FROM MovieRole WHERE name = 'Actor')),
((SELECT movie_id FROM Movie WHERE title = 'The Green Mile'), (SELECT worker_id FROM Worker WHERE surname = 'Hanks'), (SELECT role_id FROM MovieRole WHERE name = 'Actor')),
((SELECT movie_id FROM Movie WHERE title = 'The Sixth Sense'), (SELECT worker_id FROM Worker WHERE surname = 'Willis'), (SELECT role_id FROM MovieRole WHERE name = 'Actor')),
((SELECT movie_id FROM Movie WHERE title = 'Good Will Hunting'), (SELECT worker_id FROM Worker WHERE surname = 'Damon'), (SELECT role_id FROM MovieRole WHERE name = 'Actor')),
((SELECT movie_id FROM Movie WHERE title = 'The Big Lebowski'), (SELECT worker_id FROM Worker WHERE surname = 'Bridges'), (SELECT role_id FROM MovieRole WHERE name = 'Actor')),
((SELECT movie_id FROM Movie WHERE title = 'Catch Me If You Can'), (SELECT worker_id FROM Worker WHERE surname = 'DiCaprio'), (SELECT role_id FROM MovieRole WHERE name = 'Actor')),
((SELECT movie_id FROM Movie WHERE title = 'The Wolf of Wall Street'), (SELECT worker_id FROM Worker WHERE surname = 'DiCaprio'), (SELECT role_id FROM MovieRole WHERE name = 'Actor')),
((SELECT movie_id FROM Movie WHERE title = 'Shutter Island'), (SELECT worker_id FROM Worker WHERE surname = 'DiCaprio'), (SELECT role_id FROM MovieRole WHERE name = 'Actor')),
((SELECT movie_id FROM Movie WHERE title = 'Blade Runner 2049'), (SELECT worker_id FROM Worker WHERE surname = 'Gosling'), (SELECT role_id FROM MovieRole WHERE name = 'Actor')),
((SELECT movie_id FROM Movie WHERE title = 'The Social Network'), (SELECT worker_id FROM Worker WHERE surname = 'Eisenberg'), (SELECT role_id FROM MovieRole WHERE name = 'Actor')),
((SELECT movie_id FROM Movie WHERE title = 'Arrival'), (SELECT worker_id FROM Worker WHERE surname = 'Adams'), (SELECT role_id FROM MovieRole WHERE name = 'Actor')),
((SELECT movie_id FROM Movie WHERE title = 'La La Land'), (SELECT worker_id FROM Worker WHERE surname = 'Stone'), (SELECT role_id FROM MovieRole WHERE name = 'Actor')),
((SELECT movie_id FROM Movie WHERE title = 'Pulp Fiction'), (SELECT worker_id FROM Worker WHERE surname = 'Tarantino'), (SELECT role_id FROM MovieRole WHERE name = 'Actor'));

-- 10. Genres
INSERT INTO HasGenre (movie_id, genre_id)
SELECT movie_id, (SELECT genre_id FROM Genre WHERE name = 'Musical') FROM Movie WHERE title IN ('Wicked: For Good', 'Sing 2', 'La La Land');

INSERT INTO HasGenre (movie_id, genre_id)
SELECT movie_id, (SELECT genre_id FROM Genre WHERE name = 'Sci-Fi') FROM Movie WHERE title IN ('Dune: Part Two', 'Project Hail Mary', 'Inception', 'Interstellar', 'The Matrix', 'Avatar', 'Blade Runner 2049', 'Arrival', 'WALL-E', 'Alien');

INSERT INTO HasGenre (movie_id, genre_id)
SELECT movie_id, (SELECT genre_id FROM Genre WHERE name = 'Action') FROM Movie WHERE title IN ('The Dark Knight', 'Gladiator', 'The Avengers', 'Avengers: Endgame', 'Mad Max: Fury Road', 'Spider-Man: Into the Spider-Verse', 'A Minecraft Movie');

INSERT INTO HasGenre (movie_id, genre_id)
SELECT movie_id, (SELECT genre_id FROM Genre WHERE name = 'Drama') FROM Movie WHERE title IN ('Oppenheimer', 'Fight Club', 'Forrest Gump', 'The Shawshank Redemption', 'The Godfather', 'Parasite', 'Titanic', 'The Green Mile', 'Good Will Hunting', 'The Social Network', 'Whiplash');

INSERT INTO HasGenre (movie_id, genre_id)
SELECT movie_id, (SELECT genre_id FROM Genre WHERE name = 'Animation') FROM Movie WHERE title IN ('The Super Mario Bros. Movie', 'Spirited Away', 'The Lion King', 'Inside Out', 'Coco', 'Toy Story', 'WALL-E', 'Spider-Man: Into the Spider-Verse', 'Sing 2');

INSERT INTO HasGenre (movie_id, genre_id)
SELECT movie_id, (SELECT genre_id FROM Genre WHERE name = 'Thriller') FROM Movie WHERE title IN ('Se7en', 'The Silence of the Lambs', 'Shutter Island', 'The Departed', 'Joker', 'The Sixth Sense');

INSERT INTO HasGenre (movie_id, genre_id)
SELECT movie_id, (SELECT genre_id FROM Genre WHERE name = 'Crime') FROM Movie WHERE title IN ('Pulp Fiction', 'Goodfellas', 'The Godfather', 'The Departed', 'Catch Me If You Can', 'The Wolf of Wall Street');

INSERT INTO HasGenre (movie_id, genre_id)
SELECT movie_id, (SELECT genre_id FROM Genre WHERE name = 'Comedy') FROM Movie WHERE title IN ('Barbie', 'The Big Lebowski', 'The Truman Show', 'Everything Everywhere All at Once');

INSERT INTO HasGenre (movie_id, genre_id)
SELECT movie_id, (SELECT genre_id FROM Genre WHERE name = 'Adventure') FROM Movie WHERE title IN ('The Lord of the Rings: The Fellowship of the Ring', 'Jurassic Park', 'Back to the Future', 'Braveheart', 'Avatar');

INSERT INTO HasGenre (movie_id, genre_id)
SELECT movie_id, (SELECT genre_id FROM Genre WHERE name = 'Horror') FROM Movie WHERE title IN ('Dracula', 'Alien', 'The Shining');

-- 11. Audio
INSERT INTO Audio (movie_id, lang)
SELECT movie_id, (SELECT lang_id FROM Language WHERE name = 'English') FROM Movie;

INSERT INTO Audio (movie_id, lang)
SELECT movie_id, (SELECT lang_id FROM Language WHERE name = 'Italian') FROM Movie WHERE release_year >= 2015;

INSERT INTO Audio (movie_id, lang)
SELECT movie_id, (SELECT lang_id FROM Language WHERE name = 'Spanish') FROM Movie WHERE rating <= 3;

-- 12. Subtitles
INSERT INTO Subtitle (movie_id, lang)
SELECT movie_id, (SELECT lang_id FROM Language WHERE name = 'English') FROM Movie;

INSERT INTO Subtitle (movie_id, lang)
SELECT movie_id, (SELECT lang_id FROM Language WHERE name = 'Italian') FROM Movie WHERE release_year >= 1990;

INSERT INTO Subtitle (movie_id, lang)
SELECT movie_id, (SELECT lang_id FROM Language WHERE name = 'Spanish') FROM Movie WHERE running_time < 150;

-- 13. AppUser
INSERT INTO AppUser (password, email, first_name, last_name, role, created, last_update, is_active) VALUES
    ('$2a$10$UTbQcPfb0hMhkXuK/gn17eRRfEh7oxkwACeU1j7gBq6O2CpXAhKx6', 'admin@gmail.com', 'admin', 'admin',
    (SELECT id FROM UserRole WHERE description = 'Admin'),
    CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, true), 
    ('$2a$10$UTbQcPfb0hMhkXuK/gn17eRRfEh7oxkwACeU1j7gBq6O2CpXAhKx6', 'staff@gmail.com', 'staff', 'staff',
    (SELECT id FROM UserRole WHERE description = 'Staff'),
    CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, true), 
    ('$2a$10$UTbQcPfb0hMhkXuK/gn17eRRfEh7oxkwACeU1j7gBq6O2CpXAhKx6', 'prova@gmail.com', 'prova', 'prova',
    (SELECT id FROM UserRole WHERE description = 'Standard User'),
    CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, true);

-- 14. Likes
INSERT INTO Likes (user_id, movie_id) VALUES
                                        ((SELECT id FROM AppUser WHERE email = 'prova@gmail.com'), (SELECT movie_id FROM Movie WHERE title = 'Wicked: For Good')),
                                        ((SELECT id FROM AppUser WHERE email = 'prova@gmail.com'), (SELECT movie_id FROM Movie WHERE title = 'Project Hail Mary')),
                                        ((SELECT id FROM AppUser WHERE email = 'prova@gmail.com'), (SELECT movie_id FROM Movie WHERE title = 'Superman')),
                                        ((SELECT id FROM AppUser WHERE email = 'prova@gmail.com'), (SELECT movie_id FROM Movie WHERE title = 'The Matrix')),
                                        ((SELECT id FROM AppUser WHERE email = 'prova@gmail.com'), (SELECT movie_id FROM Movie WHERE title = 'The Lord of the Rings: The Fellowship of the Ring')),
                                        ((SELECT id FROM AppUser WHERE email = 'prova@gmail.com'), (SELECT movie_id FROM Movie WHERE title = 'Inception'));
