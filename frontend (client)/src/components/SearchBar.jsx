import React, { useState } from 'react'

export default function SearchBar({ onSearch, onGeolocate, loading }) {
  const [input, setInput] = useState('')
  const [geoLoading, setGeoLoading] = useState(false)
  const [geoError, setGeoError] = useState('')

  function handleSubmit(e) {
    e.preventDefault()
    const trimmed = input.trim()
    if (!trimmed) return
    onSearch(trimmed)
  }

  function handleGeolocate() {
    setGeoError('')
    if (!navigator.geolocation) {
      setGeoError('Geolocation is not supported by your browser.')
      return
    }
    setGeoLoading(true)
    navigator.geolocation.getCurrentPosition(
      (pos) => {
        setGeoLoading(false)
        const { latitude, longitude } = pos.coords
        const coords = `${latitude.toFixed(4)},${longitude.toFixed(4)}`
        setInput(coords)
        onGeolocate(coords)
      },
      (err) => {
        setGeoLoading(false)
        setGeoError('Could not get your location. Please allow location access.')
      },
      { timeout: 10000 }
    )
  }

  return (
    <div style={{ background: '#fff', borderRadius: '12px', padding: '1.25rem', boxShadow: '0 4px 6px -1px rgb(0 0 0/0.1)' }}>
      <h2 style={{ fontSize: '1rem', fontWeight: 600, marginBottom: '0.75rem', color: '#1e293b' }}>
        Search Weather
      </h2>
      <form onSubmit={handleSubmit} style={{ display: 'flex', gap: '0.5rem', flexWrap: 'wrap' }}>
        <input
          type="text"
          value={input}
          onChange={e => setInput(e.target.value)}
          placeholder="City, ZIP code, landmark, or GPS coords (lat,lon)..."
          style={{
            flex: '1 1 280px',
            padding: '0.6rem 1rem',
            border: '2px solid #e2e8f0',
            borderRadius: '8px',
            fontSize: '0.95rem',
            transition: 'border-color 0.15s',
          }}
          onFocus={e => e.target.style.borderColor = '#2563eb'}
          onBlur={e => e.target.style.borderColor = '#e2e8f0'}
          disabled={loading}
        />
        <button
          type="submit"
          disabled={loading || !input.trim()}
          style={{
            padding: '0.6rem 1.25rem',
            background: loading ? '#93c5fd' : '#2563eb',
            color: '#fff',
            borderRadius: '8px',
            fontWeight: 600,
            fontSize: '0.9rem',
          }}
        >
          {loading ? 'Searching…' : '🔍 Search'}
        </button>
        <button
          type="button"
          onClick={handleGeolocate}
          disabled={loading || geoLoading}
          title="Use my current location"
          style={{
            padding: '0.6rem 1rem',
            background: '#f0f9ff',
            color: '#0ea5e9',
            border: '2px solid #0ea5e9',
            borderRadius: '8px',
            fontWeight: 600,
            fontSize: '0.9rem',
          }}
        >
          {geoLoading ? '⏳' : '📍 My Location'}
        </button>
      </form>
      {geoError && (
        <p style={{ color: '#ef4444', fontSize: '0.8rem', marginTop: '0.5rem' }}>{geoError}</p>
      )}
      <p style={{ fontSize: '0.75rem', color: '#94a3b8', marginTop: '0.5rem' }}>
        Try: "New York", "10001", "Eiffel Tower", "35.6762,139.6503"
      </p>
    </div>
  )
}
