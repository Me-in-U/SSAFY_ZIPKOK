// tailwind.config.js
export default {
  content: ['./index.html', './src/**/*.{vue,js,ts,jsx,tsx}'],
  theme: {
    extend: {
      boxShadow: {
        // '4way'라는 이름으로 커스텀 그림자 등록
        '4way': [
          '0 2px 4px -1px rgba(0,0,0,0.05)', // 아래
          '0 -2px 4px -1px rgba(0,0,0,0.05)', // 위
          '2px 0 4px -1px rgba(0,0,0,0.05)', // 오른쪽
          '-2px 0 4px -1px rgba(0,0,0,0.05)', // 왼쪽
        ].join(', '),
      },
    },
  },
  plugins: [],
}
