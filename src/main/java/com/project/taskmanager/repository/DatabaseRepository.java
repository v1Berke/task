package com.project.taskmanager.repository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;


 // JPA dışında kalan SQLite-spesifik sorguları barındırır.

@Slf4j
@Repository
@RequiredArgsConstructor
public class DatabaseRepository {

    private final JdbcTemplate jdbc;

    // İSTATİSTİKLER

    /**
     * Kullanıcının görev istatistiklerini döner.
     * { TODO: 2, IN_PROGRESS: 1, DONE: 5 }
     */
    public Map<String, Long> getTaskStatsByUser(Long userId) {
        List<Map<String, Object>> rows = jdbc.queryForList(
                """
                SELECT status, COUNT(*) AS cnt
                FROM tasks
                WHERE user_id = ?
                GROUP BY status
                """,
                userId
        );

        Map<String, Long> stats = new java.util.LinkedHashMap<>();
        stats.put("TODO", 0L);
        stats.put("IN_PROGRESS", 0L);
        stats.put("DONE", 0L);

        for (Map<String, Object> row : rows) {
            String status = (String) row.get("status");
            Long count = ((Number) row.get("cnt")).longValue();
            stats.put(status, count);
        }
        return stats;
    }

    /**
     * Süresi geçmiş (deadline < şu an, status != DONE) görev sayısı.
     */
    public long countOverdueTasks(Long userId) {
        Long count = jdbc.queryForObject(
                """
                SELECT COUNT(*)
                FROM tasks
                WHERE user_id = ?
                  AND status != 'DONE'
                  AND deadline IS NOT NULL
                  AND deadline < datetime('now')
                """,
                Long.class,
                userId
        );
        return count != null ? count : 0L;
    }

    // VERİTABANI BAKIM


    /**
     * SQLite VACUUM — silinen kayıtların disk alanını geri al.
     * Periyodik çalıştırılabilir (örn. @Scheduled ile ayda bir).
     */
    public void vacuum() {
        log.info("VACUUM başlatılıyor...");
        jdbc.execute("VACUUM");
        log.info("VACUUM tamamlandı.");
    }

    /**
     * SQLite ANALYZE — sorgu planlamayı iyileştirmek için istatistik topla.
     */
    public void analyze() {
        log.info("ANALYZE başlatılıyor...");
        jdbc.execute("ANALYZE");
        log.info("ANALYZE tamamlandı.");
    }

    /**
     * Geçerli migration versiyonlarını listeler.
     */
    public List<Map<String, Object>> getMigrationHistory() {
        return jdbc.queryForList(
                "SELECT version, description, applied_at FROM schema_migrations ORDER BY version"
        );
    }

    /**
     * SQLite dosya boyutunu byte cinsinden döner.
     */
    public long getDatabaseSizeBytes() {
        Long pageCount = jdbc.queryForObject("PRAGMA page_count", Long.class);
        Long pageSize  = jdbc.queryForObject("PRAGMA page_size",  Long.class);
        if (pageCount == null || pageSize == null) return 0L;
        return pageCount * pageSize;
    }
}