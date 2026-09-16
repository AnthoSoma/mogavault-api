-- =============================================================================
-- SEED DATA DE DÉVELOPPEMENT (V2)
-- =============================================================================

-- 1. Utilisateur principal (NyxShade)
-- Note : password_hash fictif de test
INSERT INTO users (id, username, email, password_hash, avatar_url, bio)
VALUES (
           'a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11',
           'NyxShade',
           'nyxshade@mogavault.dev',
           '$2a$12$e8Yf.kP9QJkPwqVn12mELeO0e6Xy0A0bUv1aQ91cQj5ZkH3Xy9Z1y',
           'https://images.unsplash.com/photo-1563089145-599997674d42?w=150',
           'Chasseuse de succès et speedrunner à mes heures perdues. J''aime les RPG interminables, les boss impossibles et les couchers de soleil pixelisés.'
       ) ON CONFLICT (id) DO NOTHING;

-- 2. Comptes liés
INSERT INTO connected_accounts (user_id, platform_name, platform_user_id, platform_username, is_auto_sync_enabled)
VALUES
    ('a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11', 'STEAM', '76561198000000000', 'nyxshade', TRUE),
    ('a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11', 'PSN', 'Nyx_Shade', 'Nyx_Shade', FALSE),
    ('a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11', 'XBOX', 'NyxShadeGG', 'NyxShadeGG', FALSE),
    ('a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11', 'SWITCH', 'SW-4821-9930', 'Nyx', FALSE)
    ON CONFLICT (user_id, platform_name) DO NOTHING;

-- 3. Référentiel de jeux
INSERT INTO games (id, igdb_id, steam_app_id, title, slug, cover_url, summary, release_date)
VALUES
    (
        'b1eebc99-9c0b-4ef8-bb6d-6bb9bd380a21',
        1001,
        1245620,
        'Eldenveil: Shattered Crown',
        'eldenveil-shattered-crown',
        'https://images.unsplash.com/photo-1542751371-adc38448a05e?w=600',
        'Un action-RPG impitoyable dans un monde en ruines gouverné par des divinités déchues.',
        '2024-02-25'
    ),
    (
        'b2eebc99-9c0b-4ef8-bb6d-6bb9bd380a22',
        1002,
        NULL,
        'Neon Requiem',
        'neon-requiem',
        'https://images.unsplash.com/photo-1518709268805-4e9042af9f23?w=600',
        'Thriller cyberpunk narratif au cœur d''une mégapole sous surveillance.',
        '2023-11-10'
    ),
    (
        'b3eebc99-9c0b-4ef8-bb6d-6bb9bd380a23',
        1003,
        1091500,
        'Tidecaller Saga',
        'tidecaller-saga',
        'https://images.unsplash.com/photo-1509198397868-475647b2a1e5?w=600',
        'Aventure maritime fantastique mêlant combat tactique et gestion d''équipage.',
        '2023-08-15'
    ),
    (
        'b4eebc99-9c0b-4ef8-bb6d-6bb9bd380a24',
        1004,
        NULL,
        'Crimson Vanguard',
        'crimson-vanguard',
        'https://images.unsplash.com/photo-1550745165-9bc0b252726f?w=600',
        'Jeu de tir tactique futuriste en escouade.',
        '2024-05-18'
    )
    ON CONFLICT (id) DO NOTHING;

-- 4. Entrées de bibliothèque du joueur
INSERT INTO user_game_entries (id, user_id, game_id, status, playtime_minutes, completion_percentage, is_playtime_automatic, user_rating)
VALUES
    (
        'c1eebc99-9c0b-4ef8-bb6d-6bb9bd380a31',
        'a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11',
        'b1eebc99-9c0b-4ef8-bb6d-6bb9bd380a21',
        'CURRENTLY_PLAYING',
        6720, -- 112 heures
        78,
        TRUE,
        9
    ),
    (
        'c2eebc99-9c0b-4ef8-bb6d-6bb9bd380a32',
        'a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11',
        'b2eebc99-9c0b-4ef8-bb6d-6bb9bd380a22',
        'CURRENTLY_PLAYING',
        2760, -- 46 heures
        34,
        FALSE,
        8
    ),
    (
        'c3eebc99-9c0b-4ef8-bb6d-6bb9bd380a33',
        'a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11',
        'b3eebc99-9c0b-4ef8-bb6d-6bb9bd380a23',
        'CURRENTLY_PLAYING',
        1800, -- 30 heures
        63,
        TRUE,
        7
    ),
    (
        'c4eebc99-9c0b-4ef8-bb6d-6bb9bd380a34',
        'a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11',
        'b4eebc99-9c0b-4ef8-bb6d-6bb9bd380a24',
        'CURRENTLY_PLAYING',
        1380, -- 23 heures
        52,
        FALSE,
        NULL
    )
    ON CONFLICT (id) DO NOTHING;

-- 5. Tâches in-game (To-Do de run pour Eldenveil)
INSERT INTO game_run_tasks (user_game_entry_id, title, memo, category_tag, is_completed, position_order)
VALUES
    (
        'c1eebc99-9c0b-4ef8-bb6d-6bb9bd380a31',
        'Vaincre le Gardien de la Couronne Brisée',
        'Vulnérable aux dégâts de foudre. Prévoir des potions anti-saignement.',
        'PRIORITY',
        FALSE,
        1
    ),
    (
        'c1eebc99-9c0b-4ef8-bb6d-6bb9bd380a31',
        'Farmer 12 Éclats d''Ombrelune',
        'Spot optimal : Catacombes de l''Est, étage -2 sur les spectres.',
        'FARMING',
        FALSE,
        2
    ),
    (
        'c1eebc99-9c0b-4ef8-bb6d-6bb9bd380a31',
        'Rendre la lanterne à l''ermite de Val-Cendre',
        'PNJ présent uniquement la nuit près du feu de camp.',
        'SIDE_QUEST',
        FALSE,
        3
    ),
    (
        'c1eebc99-9c0b-4ef8-bb6d-6bb9bd380a31',
        'Améliorer l''Épée des Serments au niveau +7',
        'Matériaux nécessaires déjà en stock dans l''inventaire.',
        'PRIORITY',
        TRUE,
        4
    );

-- 6. Vitrine de succès du profil
INSERT INTO profile_achievement_showcases (user_id, game_id, title, description, rarity_label, display_order)
VALUES
    (
        'a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11',
        'b1eebc99-9c0b-4ef8-bb6d-6bb9bd380a21',
        'Seigneur des Éclats',
        'Vaincre le boss final sans subir de dégâts.',
        'LÉGENDAIRE',
        1
    ),
    (
        'a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11',
        'b3eebc99-9c0b-4ef8-bb6d-6bb9bd380a23',
        'Cœur de Cristal',
        'Collecter l''ensemble des reliques des profondeurs.',
        'RARE',
        2
    );

-- 7. Wishlist & suivi des prix
INSERT INTO wishlist_items (user_id, game_id, target_price, current_best_price, best_price_store, priority)
VALUES
    (
        'a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11',
        'b2eebc99-9c0b-4ef8-bb6d-6bb9bd380a22',
        29.99,
        34.50,
        'GoCleCD / Steam',
        'HIGH'
    );