import React from 'react'

export default function MapView({ mapInfo }) {
  if (!mapInfo) return null

  return (
    <div style={{ background: '#fff', borderRadius: '12px', overflow: 'hidden', boxShadow: '0 4px 6px -1px rgb(0 0 0/0.1)' }}>
      <div style={{ padding: '0.75rem 1.25rem', borderBottom: '1px solid #e2e8f0', display: 'flex', justifyContent: 'space-between', alignItems: 'center', flexWrap: 'wrap', gap: '0.5rem' }}>
        <h2 style={{ fontSize: '1rem', fontWeight: 600, color: '#1e293b' }}>
          🗺️ Map — {mapInfo.locationName}
        </h2>
        <a
          href={mapInfo.viewUrl}
          target="_blank"
          rel="noopener noreferrer"
          style={{
            fontSize: '0.8rem',
            color: '#2563eb',
            background: '#dbeafe',
            padding: '0.3rem 0.75rem',
            borderRadius: '6px',
            fontWeight: 500,
          }}
        >
          Open in OpenStreetMap ↗
        </a>
      </div>
      <iframe
        title={`Map of ${mapInfo.locationName}`}
        src={mapInfo.embedUrl}
        width="100%"
        height="300"
        style={{ display: 'block', border: 'none' }}
        loading="lazy"
        referrerPolicy="no-referrer-when-downgrade"
      />
      <div style={{ padding: '0.5rem 1.25rem', background: '#f8fafc', fontSize: '0.75rem', color: '#94a3b8' }}>
        © <a href="https://www.openstreetmap.org/copyright" target="_blank" rel="noopener noreferrer">OpenStreetMap</a> contributors
      </div>
    </div>
  )
}
