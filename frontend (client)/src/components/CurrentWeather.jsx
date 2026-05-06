import React from 'react'
import { celsiusToFahrenheit } from '../utils/weatherUtils'

export default function CurrentWeather({ weather, unit, onToggleUnit }) {
  if (!weather) return null

  const temp = unit === 'C' ? weather.temperature : celsiusToFahrenheit(weather.temperature)
  const feels = unit === 'C' ? weather.feelsLike : celsiusToFahrenheit(weather.feelsLike)
  const symbol = unit === 'C' ? '°C' : '°F'

  return (
    <div style={{
      background: 'linear-gradient(135deg, #1d4ed8, #0ea5e9)',
      color: '#fff',
      borderRadius: '12px',
      padding: '1.5rem',
      boxShadow: '0 10px 15px -3px rgb(0 0 0/0.1)',
    }}>
      <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'flex-start', flexWrap: 'wrap', gap: '0.5rem' }}>
        <div>
          <p style={{ fontSize: '0.85rem', opacity: 0.85 }}>📍 {weather.locationName}</p>
          <div style={{ display: 'flex', alignItems: 'center', gap: '0.5rem', marginTop: '0.25rem' }}>
            <span style={{ fontSize: '4rem', lineHeight: 1 }}>{weather.weatherEmoji}</span>
            <div>
              <p style={{ fontSize: '3rem', fontWeight: 700, lineHeight: 1 }}>
                {Math.round(temp)}{symbol}
              </p>
              <p style={{ fontSize: '1rem', opacity: 0.9 }}>{weather.description}</p>
            </div>
          </div>
          <p style={{ fontSize: '0.85rem', opacity: 0.8, marginTop: '0.35rem' }}>
            Feels like {Math.round(feels)}{symbol}
          </p>
        </div>

        <div style={{ display: 'flex', flexDirection: 'column', gap: '0.5rem', alignItems: 'flex-end' }}>
          <button
            onClick={onToggleUnit}
            style={{
              background: 'rgba(255,255,255,0.2)',
              color: '#fff',
              border: '1px solid rgba(255,255,255,0.4)',
              borderRadius: '6px',
              padding: '0.3rem 0.75rem',
              fontSize: '0.85rem',
              fontWeight: 600,
            }}
          >
            Switch to °{unit === 'C' ? 'F' : 'C'}
          </button>
          {weather.localTime && (
            <p style={{ fontSize: '0.75rem', opacity: 0.75 }}>
              Local: {weather.localTime.replace('T', ' ')}
            </p>
          )}
        </div>
      </div>

      <div style={{
        display: 'grid',
        gridTemplateColumns: 'repeat(auto-fit, minmax(100px, 1fr))',
        gap: '0.75rem',
        marginTop: '1.25rem',
        background: 'rgba(255,255,255,0.1)',
        borderRadius: '8px',
        padding: '0.75rem',
      }}>
        <Stat icon="💧" label="Humidity" value={`${weather.humidity}%`} />
        <Stat icon="💨" label="Wind" value={`${weather.windSpeed} km/h`} />
        <Stat icon="🌐" label="Lat" value={weather.latitude?.toFixed(2)} />
        <Stat icon="🌐" label="Lon" value={weather.longitude?.toFixed(2)} />
      </div>
    </div>
  )
}

function Stat({ icon, label, value }) {
  return (
    <div style={{ textAlign: 'center' }}>
      <p style={{ fontSize: '1.25rem' }}>{icon}</p>
      <p style={{ fontSize: '0.7rem', opacity: 0.75 }}>{label}</p>
      <p style={{ fontSize: '0.9rem', fontWeight: 600 }}>{value}</p>
    </div>
  )
}
