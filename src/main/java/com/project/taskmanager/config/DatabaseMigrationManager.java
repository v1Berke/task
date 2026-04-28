package com.project.taskmanager.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


/**
 * Yeni bir schema değişikliği eklemek için:
 *   getMigrations() listesine yeni bir Migration nesnesi ekle.
 *   Version numarası benzersiz ve artan olmalı.
 *
 * @Order(0) — DatabaseInitializer'dan da önce çalışır (migration tablosunu hazırlar)
 */
@Slf4j
@Component
@Order(0)
@RequiredArgsConstructor
public class DatabaseMigrationManager implements ApplicationRunner {

    private final JdbcTemplate jdbc;


    // Migration tanımı
    record Migration(String version, String description, String sql) {}


    // TÜM MİGRATION'LAR — sıralı liste
    // Yeni ekleme her zaman listeye append edilir, araya sokulamaz.
    private List<Migration> getMigrations() {
        return List.of(

                // V1 — Temel tablolar
                new Migration("V1", "users tablosu oluştur", """
                CREATE TABLE IF NOT EXISTS users (
                    id       INTEGER  PRIMARY KEY AUTOINCREMENT,
                    username TEXT     NOT NULL UNIQUE,
                    email    TEXT     NOT NULL UNIQUE,
                    password TEXT     NOT NULL
                )
            """),

                new Migration("V2", "tasks tablosu oluştur", """
                CREATE TABLE IF NOT EXISTS tasks (
                    id          INTEGER  PRIMARY KEY AUTOINCREMENT,
                    title       TEXT     NOT NULL,
                    description TEXT,
                    status      TEXT     NOT NULL DEFAULT 'TODO'
                                         CHECK (status IN ('TODO','IN_PROGRESS','DONE')),
                    deadline    DATETIME,
                    user_id     INTEGER  NOT NULL
                                         REFERENCES users(id) ON DELETE CASCADE,
                    created_at  DATETIME NOT NULL DEFAULT (datetime('now')),
                    updated_at  DATETIME NOT NULL DEFAULT (datetime('now'))
                )
            """),

                new Migration("V3", "tasks tablosuna index ekle (user_id)", """
                CREATE INDEX IF NOT EXISTS idx_tasks_user_id
                ON tasks(user_id)
            """),

                new Migration("V4", "tasks tablosuna bileşik index ekle (user_id, status)", """
                CREATE INDEX IF NOT EXISTS idx_tasks_user_status
                ON tasks(user_id, status)
            """),

                new Migration("V5", "tasks tablosuna deadline index ekle (partial)", """
                CREATE INDEX IF NOT EXISTS idx_tasks_deadline
                ON tasks(deadline)
                WHERE deadline IS NOT NULL
            """)

        );
    }

    // ANA ÇALIŞMA MANTIĞI

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        log.info(">>> Migration sistemi başlatılıyor...");

        // PRAGMA: foreign key desteğini aç
        jdbc.execute("PRAGMA foreign_keys = ON");

        // Migration kayıt tablosunu oluştur (ilk çalışmada)
        createMigrationsTable();

        // Her migration'ı kontrol et ve gerekirse uygula
        int applied = 0;
        for (Migration migration : getMigrations()) {
            if (!isMigrationApplied(migration.version())) {
                applyMigration(migration);
                applied++;
            }
        }

        if (applied == 0) {
            log.info(">>> Schema güncel, migration uygulanmadı.");
        } else {
            log.info(">>> {} migration uygulandı.", applied);
        }
    }

    // YARDIMCI METOTLAR

    private void createMigrationsTable() {
        jdbc.execute("""
            CREATE TABLE IF NOT EXISTS schema_migrations (
                version     TEXT     PRIMARY KEY,
                description TEXT     NOT NULL,
                applied_at  DATETIME NOT NULL DEFAULT (datetime('now'))
            )
        """);
    }

    private boolean isMigrationApplied(String version) {
        Integer count = jdbc.queryForObject(
                "SELECT COUNT(*) FROM schema_migrations WHERE version = ?",
                Integer.class, version
        );
        return count != null && count > 0;
    }

    private void applyMigration(Migration migration) {
        log.info("  Uygulanıyor: {} — {}", migration.version(), migration.description());
        try {
            jdbc.execute(migration.sql());
            jdbc.update(
                    "INSERT INTO schema_migrations (version, description) VALUES (?, ?)",
                    migration.version(), migration.description()
            );
            log.info("  Tamam: {}", migration.version());
        } catch (Exception e) {
            log.error("  HATA: {} migration başarısız — {}", migration.version(), e.getMessage());
            throw new RuntimeException("Migration başarısız: " + migration.version(), e);
        }
    }
}