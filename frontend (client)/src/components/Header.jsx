import React from 'react'

export default function Header({ onHome }) {
  return (
    <header style={{
      background: 'linear-gradient(135deg, #1d4ed8 0%, #0ea5e9 100%)',
      color: '#fff',
      padding: '1.25rem 1.5rem',
      display: 'flex',
      alignItems: 'center',
      justifyContent: 'space-between',
      flexWrap: 'wrap',
      gap: '0.5rem',
      boxShadow: '0 2px 8px rgba(0,0,0,0.2)',
    }}>
      <div
        onClick={onHome}
        title="Go to home"
        style={{ display: 'flex', alignItems: 'center', gap: '0.75rem', cursor: 'pointer' }}
      >
        <span style={{ fontSize: '2rem' }}>🌤️</span>
        <div>
          <h1 style={{ fontSize: '1.5rem', fontWeight: 700, lineHeight: 1 }}>WeatherApp</h1>
          <p style={{ fontSize: '0.75rem', opacity: 0.85, marginTop: '0.15rem' }}>
            Real-time weather powered by Open-Meteo
          </p>
        </div>
      </div>
      <div style={{ fontSize: '0.8rem', opacity: 0.9, textAlign: 'right' }}>
        <span>Built by <strong>Felex Hill</strong></span>
        <br />
        <span>PM Accelerator Assessment</span>
      </div>
    </header>
  )
}
