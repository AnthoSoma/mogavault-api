-- Active l'extension pour générer des UUID natifs
CREATE EXTENSION IF NOT EXISTS "pgcrypto";

-- =============================================================================
-- 1. UTILISATEURS & AUTHENTIFICATION
-- =============================================================================
CREATE TABLE users (
                       id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                       username VARCHAR(50) NOT NULL UNIQUE,
                       email VARCHAR(255) NOT NULL UNIQUE,
                       password_hash VARCHAR(255) NOT NULL,
                       avatar_url VARCHAR(500),
                       bio TEXT,
                       created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP NOT NULL,
                       updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP NOT NULL
);

-- =============================================================================
-- 2. COMPTES TIERS LIÉS (Steam, PSN, Xbox, Switch, etc.)
-- =============================================================================
CREATE TABLE connected_accounts (
                                    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                                    user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
                                    platform_name VARCHAR(30) NOT NULL, -- 'STEAM', 'PSN', 'XBOX', 'SWITCH'
                                    platform_user_id VARCHAR(100) NOT NULL, -- ex: SteamID64 ou PSN ID
                                    platform_username VARCHAR(100),
                                    is_auto_sync_enabled BOOLEAN DEFAULT FALSE NOT NULL,
                                    last_synced_at TIMESTAMP WITH TIME ZONE,
                                    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP NOT NULL,
                                    CONSTRAINT uq_user_platform UNIQUE (user_id, platform_name)
);

-- =============================================================================
-- 3. RÉFÉRENTIEL GLOBAL DES JEUX (Source IGDB / Steam)
-- =============================================================================
CREATE TABLE games (
                       id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                       igdb_id BIGINT UNIQUE,
                       steam_app_id BIGINT UNIQUE,
                       title VARCHAR(255) NOT NULL,
                       slug VARCHAR(255) NOT NULL UNIQUE,
                       cover_url VARCHAR(500),
                       banner_url VARCHAR(500),
                       summary TEXT,
                       release_date DATE,
                       created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP NOT NULL
);

-- =============================================================================
-- 4. BIBLIOTHÈQUE UTILISATEUR & STATUTS DE JEU
-- =============================================================================
CREATE TABLE user_game_entries (
                                   id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                                   user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
                                   game_id UUID NOT NULL REFERENCES games(id) ON DELETE CASCADE,
                                   status VARCHAR(30) DEFAULT 'BACKLOG' NOT NULL, -- 'CURRENTLY_PLAYING', 'BACKLOG', 'COMPLETED', 'DROPPED', 'WISHLIST'
                                   playtime_minutes INT DEFAULT 0 NOT NULL,
                                   completion_percentage INT DEFAULT 0 CHECK (completion_percentage BETWEEN 0 AND 100),
                                   is_playtime_automatic BOOLEAN DEFAULT FALSE NOT NULL,
                                   user_rating SMALLINT CHECK (user_rating BETWEEN 0 AND 10),
                                   started_at TIMESTAMP WITH TIME ZONE,
                                   completed_at TIMESTAMP WITH TIME ZONE,
                                   created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP NOT NULL,
                                   updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP NOT NULL,
                                   CONSTRAINT uq_user_game UNIQUE (user_id, game_id)
);

-- =============================================================================
-- 5. WISHLIST & SUIVI DES PRIX
-- =============================================================================
CREATE TABLE wishlist_items (
                                id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                                user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
                                game_id UUID NOT NULL REFERENCES games(id) ON DELETE CASCADE,
                                target_price NUMERIC(6, 2),
                                current_best_price NUMERIC(6, 2),
                                best_price_store VARCHAR(100), -- ex: 'GoCleCD / Steam'
                                priority VARCHAR(20) DEFAULT 'MEDIUM' NOT NULL, -- 'HIGH', 'MEDIUM', 'LOW'
                                notify_on_sale BOOLEAN DEFAULT TRUE NOT NULL,
                                created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP NOT NULL,
                                CONSTRAINT uq_user_wishlist_game UNIQUE (user_id, game_id)
);

-- =============================================================================
-- 6. TO-DO LISTS & GESTIONNAIRE D'OBJECTIFS IN-GAME
-- =============================================================================
CREATE TABLE game_run_tasks (
                                id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                                user_game_entry_id UUID NOT NULL REFERENCES user_game_entries(id) ON DELETE CASCADE,
                                title VARCHAR(255) NOT NULL,
                                memo TEXT,
                                category_tag VARCHAR(50) DEFAULT 'MAIN_QUEST' NOT NULL, -- 'PRIORITY', 'FARMING', 'SIDE_QUEST'
                                is_completed BOOLEAN DEFAULT FALSE NOT NULL,
                                position_order INT DEFAULT 0 NOT NULL,
                                created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP NOT NULL,
                                updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP NOT NULL
);

-- =============================================================================
-- 7. MÉMOS RAPIDES & BUILDS DE RUN
-- =============================================================================
CREATE TABLE game_run_notes (
                                id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                                user_game_entry_id UUID NOT NULL REFERENCES user_game_entries(id) ON DELETE CASCADE,
                                title VARCHAR(150) NOT NULL,
                                content TEXT NOT NULL,
                                created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP NOT NULL,
                                updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP NOT NULL
);

-- =============================================================================
-- 8. VITRINE DES SUCCÈS MARQUANTS (SHOWCASE PROFIL)
-- =============================================================================
CREATE TABLE profile_achievement_showcases (
                                               id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                                               user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
                                               game_id UUID REFERENCES games(id) ON DELETE SET NULL,
                                               title VARCHAR(150) NOT NULL,
                                               description TEXT,
                                               icon_url VARCHAR(500),
                                               rarity_label VARCHAR(30), -- 'LÉGENDAIRE', 'ÉPIQUE', 'RARE'
                                               display_order SMALLINT DEFAULT 1 NOT NULL,
                                               unlocked_at TIMESTAMP WITH TIME ZONE,
                                               created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP NOT NULL
);

-- =============================================================================
-- 9. CAPTURES D'ÉCRAN & CLIPS PARTAGÉS
-- =============================================================================
CREATE TABLE media_captures (
                                id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                                user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
                                game_id UUID REFERENCES games(id) ON DELETE SET NULL,
                                title VARCHAR(150) NOT NULL,
                                media_url VARCHAR(500) NOT NULL,
                                media_type VARCHAR(20) DEFAULT 'IMAGE' NOT NULL, -- 'IMAGE', 'VIDEO'
                                likes_count INT DEFAULT 0 NOT NULL,
                                created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP NOT NULL
);

-- =============================================================================
-- 10. CRITIQUES & BLOG DE RUN
-- =============================================================================
CREATE TABLE reviews (
                         id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                         user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
                         game_id UUID NOT NULL REFERENCES games(id) ON DELETE CASCADE,
                         rating SMALLINT CHECK (rating BETWEEN 0 AND 10),
                         review_title VARCHAR(200),
                         content TEXT NOT NULL,
                         is_spoiler BOOLEAN DEFAULT FALSE NOT NULL,
                         created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP NOT NULL,
                         updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP NOT NULL,
                         CONSTRAINT uq_user_game_review UNIQUE (user_id, game_id)
);

-- =============================================================================
-- 11. AMITIÉS & LIENS SOCIAUX
-- =============================================================================
CREATE TABLE friendships (
                             id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                             sender_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
                             receiver_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
                             status VARCHAR(20) DEFAULT 'PENDING' NOT NULL, -- 'PENDING', 'ACCEPTED', 'BLOCKED'
                             created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP NOT NULL,
                             CONSTRAINT uq_friendship_pair UNIQUE (sender_id, receiver_id),
                             CONSTRAINT chk_not_self_friending CHECK (sender_id <> receiver_id)
);

-- =============================================================================
-- INDEX DE RECHERCHE ET PERFORMANCES
-- =============================================================================
CREATE INDEX idx_user_game_entries_user ON user_game_entries(user_id);
CREATE INDEX idx_user_game_entries_status ON user_game_entries(status);
CREATE INDEX idx_game_run_tasks_entry ON game_run_tasks(user_game_entry_id);
CREATE INDEX idx_wishlist_items_user ON wishlist_items(user_id);
CREATE INDEX idx_media_captures_user ON media_captures(user_id);
CREATE INDEX idx_games_slug ON games(slug);