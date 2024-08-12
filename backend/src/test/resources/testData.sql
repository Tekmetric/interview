-- Valid game, one move from completion
INSERT INTO game (id, status, total_moves, board_size, last_move, forward_diagonal_sum, backward_diagonal_sum) VALUES (1, 'IN_PROGRESS', 8, 3, 1, -1, 0);
INSERT INTO game_grid_square (id, game_id, grid_row, grid_col, grid_val) VALUES (1, 1, 0, 0, 1);
INSERT INTO game_grid_square (id, game_id, grid_row, grid_col, grid_val) VALUES (2, 1, 0, 1, -1);
INSERT INTO game_grid_square (id, game_id, grid_row, grid_col, grid_val) VALUES (3, 1, 0, 2, 1);
INSERT INTO game_grid_square (id, game_id, grid_row, grid_col, grid_val) VALUES (4, 1, 1, 0, 1);
INSERT INTO game_grid_square (id, game_id, grid_row, grid_col, grid_val) VALUES (5, 1, 1, 1, -1);
INSERT INTO game_grid_square (id, game_id, grid_row, grid_col, grid_val) VALUES (6, 1, 1, 2, 1);
INSERT INTO game_grid_square (id, game_id, grid_row, grid_col, grid_val) VALUES (7, 1, 2, 0, -1);
INSERT INTO game_grid_square (id, game_id, grid_row, grid_col, grid_val) VALUES (8, 1, 2, 1, 1);
INSERT INTO game_grid_square (id, game_id, grid_row, grid_col, grid_val) VALUES (9, 1, 2, 2, 0);
INSERT INTO game_line (id, game_id, is_row, line_id, line_sum) VALUES (1, 1, true, 0, 1);
INSERT INTO game_line (id, game_id, is_row, line_id, line_sum) VALUES (2, 1, true, 1, 1);
INSERT INTO game_line (id, game_id, is_row, line_id, line_sum) VALUES (3, 1, true, 2, 0);
INSERT INTO game_line (id, game_id, is_row, line_id, line_sum) VALUES (4, 1, false, 0, 1);
INSERT INTO game_line (id, game_id, is_row, line_id, line_sum) VALUES (5, 1, false, 1, -1);
INSERT INTO game_line (id, game_id, is_row, line_id, line_sum) VALUES (6, 1, false, 2, 2);

-- Valid game, in the middle
INSERT INTO game (id, status, total_moves, board_size, last_move, forward_diagonal_sum, backward_diagonal_sum) VALUES (2, 'IN_PROGRESS', 5, 3, 1, 0, 0);
INSERT INTO game_grid_square (id, game_id, grid_row, grid_col, grid_val) VALUES (10, 2, 0, 0, 1);
INSERT INTO game_grid_square (id, game_id, grid_row, grid_col, grid_val) VALUES (11, 2, 0, 1, 0);
INSERT INTO game_grid_square (id, game_id, grid_row, grid_col, grid_val) VALUES (12, 2, 0, 2, -1);
INSERT INTO game_grid_square (id, game_id, grid_row, grid_col, grid_val) VALUES (13, 2, 1, 0, 1);
INSERT INTO game_grid_square (id, game_id, grid_row, grid_col, grid_val) VALUES (14, 2, 1, 1, 1);
INSERT INTO game_grid_square (id, game_id, grid_row, grid_col, grid_val) VALUES (15, 2, 1, 2, -1);
INSERT INTO game_grid_square (id, game_id, grid_row, grid_col, grid_val) VALUES (16, 2, 2, 0, 0);
INSERT INTO game_grid_square (id, game_id, grid_row, grid_col, grid_val) VALUES (17, 2, 2, 1, 0);
INSERT INTO game_grid_square (id, game_id, grid_row, grid_col, grid_val) VALUES (18, 2, 2, 2, 0);
INSERT INTO game_line (id, game_id, is_row, line_id, line_sum) VALUES (7, 2, true, 0, 0);
INSERT INTO game_line (id, game_id, is_row, line_id, line_sum) VALUES (8, 2, true, 1, 1);
INSERT INTO game_line (id, game_id, is_row, line_id, line_sum) VALUES (9, 2, true, 2, 0);
INSERT INTO game_line (id, game_id, is_row, line_id, line_sum) VALUES (10, 2, false, 0, 2);
INSERT INTO game_line (id, game_id, is_row, line_id, line_sum) VALUES (11, 2, false, 1, 1);
INSERT INTO game_line (id, game_id, is_row, line_id, line_sum) VALUES (12, 2, false, 2, -2);

-- Invalid game, no game_grid_square or game_line values given, to test the corrupted database case
INSERT INTO game (id, status, total_moves, board_size, last_move, forward_diagonal_sum, backward_diagonal_sum) VALUES (3, 'IN_PROGRESS', 5, 3, 1, 0, 0);
