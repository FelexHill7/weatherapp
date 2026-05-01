import React from 'react'

export default function AboutSection() {
  return (
    <footer style={{
      background: '#1e293b',
      color: '#e2e8f0',
      borderRadius: '12px',
      padding: '1.5rem',
      marginTop: '1rem',
    }}>
      <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fit, minmax(260px, 1fr))', gap: '1.5rem' }}>
        <div>
          <h3 style={{ fontSize: '0.9rem', fontWeight: 700, marginBottom: '0.5rem', color: '#f8fafc' }}>
            🌤️ About This App
          </h3>
          <p style={{ fontSize: '0.8rem', lineHeight: 1.6, color: '#94a3b8' }}>
            A full-stack weather application built by <strong style={{ color: '#e2e8f0' }}>Felex Hill</strong> for the
            PM Accelerator AI Engineer Intern Technical Assessment.
          </p>
          <p style={{ fontSize: '0.8rem', lineHeight: 1.6, color: '#94a3b8', marginTop: '0.5rem' }}>
            Powered by <strong style={{ color: '#e2e8f0' }}>Open-Meteo</strong> (free, no API key) for real-time and
            historical weather, <strong style={{ color: '#e2e8f0' }}>OpenStreetMap</strong> for maps, and
            <strong style={{ color: '#e2e8f0' }}> Spring Boot 4 + PostgreSQL</strong> on the backend.
          </p>
        </div>

        <div>
          <h3 style={{ fontSize: '0.9rem', fontWeight: 700, marginBottom: '0.5rem', color: '#f8fafc' }}>
            🚀 About PM Accelerator
          </h3>
          <p style={{ fontSize: '0.8rem', lineHeight: 1.6, color: '#94a3b8' }}>
            <strong style={{ color: '#e2e8f0' }}>Product Manager Accelerator</strong> is the #1 PM coaching and
            community platform, helping thousands of aspiring and experienced PMs break into product management,
            level up their careers, and build the skills, network, and confidence to succeed in the world of
            product management through mentorship, real-world projects, and an elite global community.
          </p>
          <a
            href="https://www.linkedin.com/company/product-manager-accelerator/"
            target="_blank"
            rel="noopener noreferrer"
            style={{
              display: 'inline-block',
              marginTop: '0.5rem',
              fontSize: '0.8rem',
              color: '#60a5fa',
              background: 'rgba(96,165,250,0.1)',
              padding: '0.3rem 0.75rem',
              borderRadius: '6px',
            }}
          >
            🔗 PM Accelerator on LinkedIn
          </a>
        </div>

        <div>
          <h3 style={{ fontSize: '0.9rem', fontWeight: 700, marginBottom: '0.5rem', color: '#f8fafc' }}>
            🛠️ Tech Stack
          </h3>
          <ul style={{ fontSize: '0.8rem', color: '#94a3b8', lineHeight: 2, listStyle: 'none' }}>
            <li>⚛️ <strong style={{ color: '#e2e8f0' }}>React 18</strong> + Vite (Frontend)</li>
            <li>☕ <strong style={{ color: '#e2e8f0' }}>Spring Boot 4</strong> + Java 21 (Backend)</li>
            <li>🐘 <strong style={{ color: '#e2e8f0' }}>PostgreSQL</strong> (Database)</li>
            <li>🌍 <strong style={{ color: '#e2e8f0' }}>Open-Meteo API</strong> (Weather)</li>
            <li>🗺️ <strong style={{ color: '#e2e8f0' }}>OpenStreetMap</strong> (Maps)</li>
            <li>🎬 <strong style={{ color: '#e2e8f0' }}>YouTube Data API v3</strong> (Videos)</li>
          </ul>
        </div>
      </div>

      <div style={{ borderTop: '1px solid #334155', marginTop: '1rem', paddingTop: '0.75rem', textAlign: 'center', fontSize: '0.75rem', color: '#64748b' }}>
        Assessment #1 (Frontend) + Assessment #2 (Backend) · Built May 2026 · Felex Hill
      </div>
    </footer>
  )
}
